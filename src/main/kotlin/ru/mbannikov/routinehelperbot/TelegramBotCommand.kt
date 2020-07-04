package ru.mbannikov.routinehelperbot

import com.github.kotlintelegrambot.CommandHandleUpdate
import com.github.kotlintelegrambot.HandleUpdate

interface TelegramBotCommand {
    val command: String

    val handler: HandleUpdate
}

interface TelegramBotCommandWithArgs {
    val command: String

    val handler: CommandHandleUpdate
}
