package ru.mbannikov.routinehelperbot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.entities.ParseMode
import okhttp3.logging.HttpLoggingInterceptor
import org.springframework.stereotype.Component
import ru.mbannikov.routinehelperbot.config.property.TelegramApiProperties
import ru.mbannikov.routinehelperbot.config.property.TelegramProperties
import ru.mbannikov.routinehelperbot.utils.Logging

@Component
class TelegramBot(
    private val properties: TelegramProperties,
    private val apiProperties: TelegramApiProperties,
    private val commandsWithArgs: Collection<TelegramBotCommandWithArgs>
) : Logging {
    private val bot: Bot = bot {
        token = apiProperties.botToken
        logLevel = HttpLoggingInterceptor.Level.NONE

        dispatch {
            commandsWithArgs.forEach { cmd ->
                command(cmd.command, cmd.handler)
            }
        }
    }.also { it.startPolling() }

    fun sendMessage(text: String) {
        bot.sendMessage(chatId = properties.ownerChatId, text = text, parseMode = ParseMode.MARKDOWN)
    }
}
