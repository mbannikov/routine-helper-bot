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
import kotlin.reflect.KFunction1

@Component
class TelegramBotTimeCommand(
    private val measureTimeLogRepository: MeasureTimeLogRepository
) : TelegramBotCommandWithArgs, Logging {
    override val command: String = COMMAND

    override val handler: CommandHandleUpdate = { bot, update, args ->
        try {
            handle(bot, update, args)
        } catch (e: CommandWithoutArgsException) {
            sendHelpMessage(bot, update)
        } catch (e: Throwable) {
            log.error(e) { "Unknown error" }
            val message = "Произошла неизвестная ошибка: ${e.message ?: e.javaClass.simpleName}"
            sendMessage(bot, update, message)
        }
    }

    private val timeMeasurer = TimeMeasurer()

    private fun handle(bot: Bot, update: Update, args: List<String>) {
        val action = args.firstOrNull()
        when (action) {
            START_ACTION -> handleStartAction(bot, update)
            FINISH_ACTION -> handleFinishAction(bot, update)
            MINUS_ACTION -> handleMinusAction(bot, update, args.subList(1, args.size))
            PLUS_ACTION -> handlePlusAction(bot, update, args.subList(1, args.size))
            STAT_ACTION -> handleStatAction(bot, update)
            null -> sendHelpMessage(bot, update)
        }
    }

    private fun handleStartAction(bot: Bot, update: Update) {
        log.info { "got command \"/$command $START_ACTION\"" }

        timeMeasurer.start()
        sendMessage(bot, update, text = "Таймер запущен")
    }

    private fun handleFinishAction(bot: Bot, update: Update) {
        log.info { "got command \"/$command $FINISH_ACTION\"" }

        val duration = timeMeasurer.finish().also(measureTimeLogRepository::saveMeasureTime)
        val todayDuration = measureTimeLogRepository.getTodayDuration()
        val thisWeekDuration = measureTimeLogRepository.getThisWeekDuration()

        val responseMessage = MessageBuilder.buildFinishActionMessage(
            now = duration,
            today = todayDuration,
            thisWeek = thisWeekDuration
        )
        sendMessage(bot, update, responseMessage)
    }

    private fun handleMinusAction(bot: Bot, update: Update, args: List<String>) {
        log.info { "got command \"/$command $MINUS_ACTION\" with args $args" }

        handlePlusOrMinusAction(bot, update, args, measureTimeLogRepository::saveNegativeMeasureTime)
    }

    private fun handlePlusAction(bot: Bot, update: Update, args: List<String>) {
        log.info { "got command \"/$command $PLUS_ACTION\" with args $args" }

        handlePlusOrMinusAction(bot, update, args, measureTimeLogRepository::saveMeasureTime)
    }

    private fun handlePlusOrMinusAction(
        bot: Bot,
        update: Update,
        args: List<String>,
        handler: KFunction1<@ParameterName(name = "duration") Duration, Unit>
    ) {
        val minutes = args.firstOrNull()?.toLong() ?: throw CommandWithoutArgsException()
        val duration = Duration.ofMinutes(minutes)

        handler(duration)
        sendStatisticMessage(bot, update)
    }

    private fun handleStatAction(bot: Bot, update: Update) {
        log.info { "got command \"/$command $STAT_ACTION\"" }

        sendStatisticMessage(bot, update)
    }

    private fun sendStatisticMessage(bot: Bot, update: Update) {
        val todayDuration = measureTimeLogRepository.getTodayDuration()
        val thisWeekDuration = measureTimeLogRepository.getThisWeekDuration()

        val responseMessage = MessageBuilder.buildStatisticMessage(todayDuration, thisWeekDuration)
        sendMessage(bot, update, responseMessage)
    }

    private fun sendHelpMessage(bot: Bot, update: Update) {
        log.info { "got command \"/$command\"" }

        val responseMessage = MessageBuilder.buildHelpMessage()
        sendMessage(bot, update, responseMessage)
    }

    private fun sendMessage(bot: Bot, update: Update, text: String) {
        bot.sendMessage(chatId = update.message!!.chat.id, text = text, parseMode = ParseMode.MARKDOWN)
    }

    companion object {
        internal const val COMMAND = "timer"
        internal const val START_ACTION = "start"
        internal const val FINISH_ACTION = "finish"
        internal const val MINUS_ACTION = "minus"
        internal const val PLUS_ACTION = "plus"
        internal const val STAT_ACTION = "stat"
    }
}

private class MessageBuilder private constructor() {
    companion object {
        private const val command: String = TelegramBotTimeCommand.COMMAND

        fun buildHelpMessage() = """*Начать замер*:
            |/$command ${TelegramBotTimeCommand.START_ACTION}
            |
            |*Закончить замер*: 
            |/$command ${TelegramBotTimeCommand.FINISH_ACTION}
            |
            |*Отнять продолжительность минут* (на случай, если забыл завершить замер):
            |/$command ${TelegramBotTimeCommand.MINUS_ACTION} %MIN%
            |
            |*Прибавить продолжительность минут* (на случай, если замер не делался):
            |/$command ${TelegramBotTimeCommand.PLUS_ACTION} %MIN%
            |
            |*Показать статистику за день и текущую неделю:*
            |/$command ${TelegramBotTimeCommand.STAT_ACTION}
        """.trimMargin()

        fun buildFinishActionMessage(now: Duration, today: Duration, thisWeek: Duration): String =
            try {
                val nowSpentTime = buildSpentTimeString(now)
                val todaySpentTime = buildSpentTimeString(today)
                val thisWeekSpentTime = buildSpentTimeString(thisWeek)

                """Сейчас: $nowSpentTime
                |В течении дня: $todaySpentTime
                |В течении недели: $thisWeekSpentTime
            """.trimMargin()
            } catch (e: TimerHasToBeStartedException) {
                "Ранее таймер не был запущен."
            }

        fun buildStatisticMessage(todayDuration: Duration, thisWeekDuration: Duration): String {
            val todaySpentTime = buildSpentTimeString(todayDuration)
            val thisWeekSpentTime = buildSpentTimeString(thisWeekDuration)

            return """В течении дня: $todaySpentTime
                |В течении недели: $thisWeekSpentTime
            """.trimMargin()
        }

        fun buildSpentTimeString(duration: Duration): String {
            val durationInMinutes = duration.toMinutes()
            val hours = durationInMinutes / 60
            val minutes = durationInMinutes - (hours * 60)

            return when (hours) {
                0L -> "$minutes мин."
                else -> "$hours ч. $minutes мин."
            }
        }
    }
}
