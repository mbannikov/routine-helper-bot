package ru.mbannikov.routinehelperbot.taskreminder.repository

import me.theforbiddenai.trellowrapperkotlin.TrelloApi
import me.theforbiddenai.trellowrapperkotlin.objects.TrelloList
import org.springframework.stereotype.Component
import ru.mbannikov.routinehelperbot.config.property.TrelloApiProperties
import ru.mbannikov.routinehelperbot.config.property.TrelloDayTaskListProperties
import ru.mbannikov.routinehelperbot.config.property.TrelloListProperties
import ru.mbannikov.routinehelperbot.config.property.TrelloWeekGoalListProperties
import ru.mbannikov.routinehelperbot.taskreminder.entity.Task
import ru.mbannikov.routinehelperbot.taskreminder.entity.TaskLabel
import ru.mbannikov.routinehelperbot.taskreminder.entity.TaskLabel.DAY
import ru.mbannikov.routinehelperbot.taskreminder.entity.TaskLabel.WEEK_GOALS

@Component
class TrelloTaskRepository(
    apiProperties: TrelloApiProperties,
    taskListProperties: TrelloDayTaskListProperties,
    weekGoalListProperties: TrelloWeekGoalListProperties
) : TaskRepository {
    private val api = TrelloApi(apiKey = apiProperties.key, token = apiProperties.token)
    private val tasksTrelloList by lazy { findTrelloList(taskListProperties) }
    private val goalsTrelloList by lazy { findTrelloList(weekGoalListProperties) }

    override fun getListByLabel(label: TaskLabel): List<Task> =
        when (label) {
            DAY -> getDayTasks()
            WEEK_GOALS -> getWeekGoals()
        }

    private fun getDayTasks(): List<Task> = buildTaskList(tasksTrelloList, DAY)

    private fun getWeekGoals(): List<Task> = buildTaskList(goalsTrelloList, WEEK_GOALS)

    private fun buildTaskList(trelloList: TrelloList, label: TaskLabel): List<Task> =
        trelloList.getCards().map {
            Task(title = it.name, description = it.desc.ifEmpty { null }, label = label)
        }

    private fun findTrelloList(props: TrelloListProperties): TrelloList =
        api.getBoard(props.boardId).let { board ->
            board.getLists()
                .find { it.name == props.listName }
                ?: throw IllegalStateException(
                    "List with name \"${props.listName}\" not found on board id \"${props.boardId}\"."
                )
        }
}
