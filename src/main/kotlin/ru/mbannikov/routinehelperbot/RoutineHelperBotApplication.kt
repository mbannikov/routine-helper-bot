package ru.mbannikov.routinehelperbot

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import ru.mbannikov.routinehelperbot.config.property.TaskProperties
import ru.mbannikov.routinehelperbot.taskreminder.job.TasksReminderJob
import java.util.Timer
import kotlin.concurrent.schedule

@SpringBootApplication
@ConfigurationPropertiesScan
class RoutineHelperBotApplication(
    private val tasksReminderJob: TasksReminderJob,
    private val taskProperties: TaskProperties
) : CommandLineRunner {
    override fun run(vararg args: String) {
        val taskReminderPeriod = taskProperties.messageFrequency.toMillis()
        Timer("TaskReminder", false).schedule(delay = 0, period = taskReminderPeriod) {
            tasksReminderJob.execute()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<RoutineHelperBotApplication>(*args)
}
