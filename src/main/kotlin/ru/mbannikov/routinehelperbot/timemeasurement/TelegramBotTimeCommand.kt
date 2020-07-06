package ru.mbannikov.routinehelperbot.timemeasurement

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.CommandHandleUpdate
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.Update
import org.springframework.stereotype.Component
import ru.mbannikov.routinehelperbot.TelegramBotCommandWithArgs
import ru.mbannikov.routinehelperbot.timemeasurement.repository.MeasureTimeLogRepository
import ru.mbannikov.routinehelperbot.utils.Logging
import java.time.Duration

@Component
class TelegramBotTimeCommand(
    private val measureTimeLogRepository: MeasureTimeLogRepository
) : TelegramBotCommandWithArgs, Logging {
    override val command: String = "timer"

    override val handler: CommandHandleUpdate = { bot, update, args ->
        try {
            handle(bot, update, args)
        } catch (e: MinusCommandWithoutArgs) {
            sendHelpMessage(bot, update)
        } catch (e: Throwable) {
            log.error(e) { "Unknown error" }
            val message = "Произошла неизвестная ошибка: ${e.message ?: e.javaClass.simpleName}"
            bot.sendMessage(chatId = update.message!!.chat.id, text = message)
        }
    }

    private val timeMeasurer = TimeMeasurer()

    private fun handle(bot: Bot, update: Update, args: List<String>) {
        val action = args.firstOrNull()
        when (action) {
            START_ACTION -> handleStartAction(bot, update)
            FINISH_ACTION -> handleFinishAction(bot, update)
            MINUS_ACTION -> handleMinusAction(bot, update, args.subList(1, args.size))
            null -> sendHelpMessage(bot, update)
        }
    }

    private fun handleStartAction(bot: Bot, update: Update) {
        log.info { "got command \"/$command $START_ACTION\"" }

        timeMeasurer.start()
        bot.sendMessage(chatId = update.message!!.chat.id, text = "Таймер запущен")
    }

    private fun handleFinishAction(bot: Bot, update: Update) {
        log.info { "got command \"/$command $FINISH_ACTION\"" }

        val duration = timeMeasurer.finish().also(measureTimeLogRepository::saveMeasureTime)
        val todayDuration = measureTimeLogRepository.getTodayDuration()
        val thisWeekDuration = measureTimeLogRepository.getThisWeekDuration()

        val responseMessage = buildFinishActionMessage(
            now = duration,
            today = todayDuration,
            thisWeek = thisWeekDuration
        )

        bot.sendMessage(chatId = update.message!!.chat.id, text = responseMessage)
    }

    private fun handleMinusAction(bot: Bot, update: Update, args: List<String>) {
        log.info { "got command \"/$command $MINUS_ACTION\" with args $args" }

        val minutes = args.firstOrNull()?.toLong() ?: throw MinusCommandWithoutArgs()
        val duration = Duration.ofMinutes(minutes)

        measureTimeLogRepository.saveNegativeMeasureTime(duration)
    }

    private fun sendHelpMessage(bot: Bot, update: Update) {
        log.info { "got command \"/$command\"" }

        val responseMessage = """*Начать замер*:
            |/$command $START_ACTION
            |
            |*Закончить замер*: 
            |/$command $FINISH_ACTION
            |
            |*Отнять продолжительность минут* (на случай, если забыл завершить замер):
            |/$command $MINUS_ACTION %MIN%
        """.trimMargin()

        bot.sendMessage(chatId = update.message!!.chat.id, text = responseMessage, parseMode = ParseMode.MARKDOWN)
    }

    private fun buildFinishActionMessage(now: Duration, today: Duration, thisWeek: Duration): String =
        try {
            val nowSpentTime = buildSpentTimeString(now)
            val todaySpentTime = buildSpentTimeString(today)
            val thisWeekSpentTime = buildSpentTimeString(thisWeek)

            """Сейчас: $nowSpentTime
                |В течении дня: $todaySpentTime
                |В течении недели: $thisWeekSpentTime
            """.trimMargin()
        } catch (e: TimerWasNotStarted) {
            "Ранее таймер не был запущен."
        }

    private fun buildSpentTimeString(duration: Duration): String {
        val durationInMinutes = duration.toMinutes()
        val hours = durationInMinutes / 60
        val minutes = durationInMinutes - (hours * 60)

        return when (hours) {
            0L -> "$minutes мин."
            else -> "$hours ч. $minutes мин."
        }
    }

    companion object {
        private const val START_ACTION = "start"
        private const val FINISH_ACTION = "finish"
        private const val MINUS_ACTION = "minus"
    }
}
