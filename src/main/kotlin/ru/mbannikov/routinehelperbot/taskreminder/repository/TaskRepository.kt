package ru.mbannikov.routinehelperbot.taskreminder.repository

import ru.mbannikov.routinehelperbot.taskreminder.entity.Task
import ru.mbannikov.routinehelperbot.taskreminder.entity.TaskLabel

interface TaskRepository {
    fun getListByLabel(label: TaskLabel): List<Task>
}
