package ru.mbannikov.routinehelperbot.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("telegram.api")
data class TelegramApiProperties(
    val botToken: String
)

@ConstructorBinding
@ConfigurationProperties("telegram")
data class TelegramProperties(
    val ownerChatId: Long
)
