package ru.mbannikov.routinehelperbot.taskreminder.entity

data class Task(
    val title: String,
    val description: String?,
    val label: TaskLabel
)