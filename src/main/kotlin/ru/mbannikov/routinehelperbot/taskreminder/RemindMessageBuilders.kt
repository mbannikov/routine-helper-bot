package ru.mbannikov.routinehelperbot.taskreminder

import org.springframework.stereotype.Component
import ru.mbannikov.routinehelperbot.taskreminder.entity.TaskLabel
import ru.mbannikov.routinehelperbot.taskreminder.repository.TaskRepository

interface RemindMessageBuilders {
    fun build(): String?
}

@Component
class TaskListRemindMessageBuilder(
    private val taskRepository: TaskRepository
) : RemindMessageBuilders {
    override fun build(): String? {
        val tasks = taskRepository.getListByLabel(TaskLabel.DAY).ifEmpty { null }

        return tasks?.mapIndexed { index, task -> "${index + 1}. ${task.title}" }
            ?.joinToString(separator = "\n", prefix = "$TASKS_EMOJI *Задачи на сегодня:*\n")
    }

    companion object {
        private const val TASKS_EMOJI = "\uD83D\uDCCB"
    }
}

@Component
class GoalListRemindMessageBuilder(
    private val taskRepository: TaskRepository
) : RemindMessageBuilders {
    override fun build(): String? {
        val goals = taskRepository.getListByLabel(TaskLabel.WEEK_GOALS).ifEmpty { null }

        return goals?.mapIndexed { index, task -> "${index + 1}. ${task.title}" }
            ?.joinToString(separator = "\n", prefix = "$GOALS_EMOJI *Цели этой недели:*\n")
    }

    companion object {
        private const val GOALS_EMOJI = "\uD83D\uDCC8"
    }
}
