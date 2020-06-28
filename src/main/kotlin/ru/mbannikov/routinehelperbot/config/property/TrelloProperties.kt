package ru.mbannikov.routinehelperbot.config.property

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties("trello.api")
data class TrelloApiProperties(
    val key: String,
    val token: String
)

interface TrelloListProperties {
    val boardId: String
    val listName: String
}

@ConstructorBinding
@ConfigurationProperties("trello.day-task-list")
data class TrelloDayTaskListProperties(
    override val boardId: String,
    override val listName: String
) : TrelloListProperties

@ConstructorBinding
@ConfigurationProperties("trello.week-goal-list")
data class TrelloWeekGoalListProperties(
    override val boardId: String,
    override val listName: String
) : TrelloListProperties
