package ru.mbannikov.routinehelperbot.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.ZoneId

@ConstructorBinding
@ConfigurationProperties("application")
data class CommonApplicationProperties(
    val timeZone: ZoneId
)
