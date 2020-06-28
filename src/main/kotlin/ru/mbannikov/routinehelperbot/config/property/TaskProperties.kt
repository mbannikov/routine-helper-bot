package ru.mbannikov.routinehelperbot.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration
import java.time.LocalTime

@ConstructorBinding
@ConfigurationProperties("application.tasks")
data class TaskProperties(
    val messageFrequency: Duration,
    val notificationPeriod: NotificationPeriod
) {
    data class NotificationPeriod(
        val start: LocalTime,
        val finish: LocalTime
    )
}
