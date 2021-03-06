package ru.mbannikov.routinehelperbot.taskreminder.job

import org.springframework.stereotype.Component
import ru.mbannikov.routinehelperbot.TelegramBot
import ru.mbannikov.routinehelperbot.config.property.CommonApplicationProperties
import ru.mbannikov.routinehelperbot.config.property.TaskProperties
import ru.mbannikov.routinehelperbot.taskreminder.GoalListRemindMessageBuilder
import ru.mbannikov.routinehelperbot.taskreminder.TaskListRemindMessageBuilder
import ru.mbannikov.routinehelperbot.utils.Logging
import java.time.LocalTime

@Component
class TasksReminderJob(
    private val taskListMessageBuilder: TaskListRemindMessageBuilder,
    private val goalListMessageBuilder: GoalListRemindMessageBuilder,
    private val telegramBot: TelegramBot,
    private val taskProperties: TaskProperties,
    private val commonProperties: CommonApplicationProperties
) : Logging {
    fun execute() {
        if (isSilentTime) {
            log.debug { "Now is a silent time" }
            return
        }

        listOf(taskListMessageBuilder, goalListMessageBuilder)
            .mapNotNull { it.build() }
            .forEach { telegramBot.sendMessage(it) }

        log.debug { "A reminder message was sent" }
    }

    private val isSilentTime: Boolean
        get() {
            val now = LocalTime.now(commonProperties.timeZone)
            return !(now.isAfter(taskProperties.notificationPeriod.start) && now.isBefore(taskProperties.notificationPeriod.finish))
        }
}
