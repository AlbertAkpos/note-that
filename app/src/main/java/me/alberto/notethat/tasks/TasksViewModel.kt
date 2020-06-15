package me.alberto.notethat.tasks

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import me.alberto.notethat.Event
import me.alberto.notethat.R
import me.alberto.notethat.data.Result
import me.alberto.notethat.data.Result.Success
import me.alberto.notethat.data.Task
import me.alberto.notethat.data.source.TasksRepository

class TasksViewModel(private val tasksRepository: TasksRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _forceUpdate = MutableLiveData<Boolean>(false)


    private val _items: LiveData<List<Task>> = _forceUpdate.switchMap { forceUpdate ->
        if (forceUpdate) {
            _dataLoading.value = true
            viewModelScope.launch {
                tasksRepository.refreshTasks()
                _dataLoading.value = false
            }
        }
        tasksRepository.observeTasks().switchMap { filterTasks(it) }
    }

    val items: LiveData<List<Task>> = _items

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noTasksLabel = MutableLiveData<Int>()
    val noTasksLabel: LiveData<Int> = _noTasksLabel

    private val _noTasksIconRes = MutableLiveData<Int>()
    val noTaskIconRes: LiveData<Int> = _noTasksIconRes

    private val _tasksAddViewVisible = MutableLiveData<Boolean>()
    val tasksAddViewVisible: LiveData<Boolean> = _tasksAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    private var currentFiltering = TasksFilterType.ALL_TASKS

    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openTaskEvent = MutableLiveData<Event<String>>()
    val openTaskEvent: LiveData<Event<String>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    private var resultMessageShown: Boolean = false

    val empty = Transformations.map(_items) { it.isEmpty() }


    init {
        //Set initial state
        setFiltering(TasksFilterType.ALL_TASKS)
        loadTasks(true)
    }

    fun loadTasks(forceUpdate: Boolean) {
        _forceUpdate.value = forceUpdate
    }

    fun setFiltering(requestType: TasksFilterType) {
        currentFiltering = requestType

        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                setFilter(
                    R.string.label_all, R.string.no_tasks_all,
                    R.drawable.logo_no_fill, true
                )
            }

            TasksFilterType.ACTIVE_TASKS -> {
                setFilter(
                    R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp, false
                )
            }

            TasksFilterType.COMPLETED_TASKS -> {
                setFilter(
                    R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp, false
                )
            }
        }

        loadTasks(false)
    }

    private fun setFilter(
        @StringRes filteringLabelString: Int,
        @StringRes noTaskLabelString: Int, @DrawableRes noTaskIconDrawable: Int,
        tasksAddVisible: Boolean
    ) {
        _currentFilteringLabel.value = filteringLabelString
        _noTasksLabel.value = noTaskLabelString
        _noTasksIconRes.value = noTaskIconDrawable
        _tasksAddViewVisible.value = tasksAddVisible
    }


    fun clearCompletedTasks() {
        viewModelScope.launch {
            tasksRepository.clearCompletedTasks()
            showSnackbarMessage(R.string.completed_tasks_cleared)
        }
    }

    fun completeTask(task: Task, completed: Boolean) {
        viewModelScope.launch {
            if (completed) {
                tasksRepository.completeTask(task)
                showSnackbarMessage(R.string.task_marked_complete)
            } else {
                tasksRepository.activateTask(task)
                showSnackbarMessage(R.string.task_marked_active)
            }
        }
    }

    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

    fun openTask(taskId: String) {
        _openTaskEvent.value = Event(taskId)
    }

    fun showEditResultMessage(result: Int) {
        if (resultMessageShown) return
        when (result) {
            EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_saved_task_message)
            ADD_EDIT_RESULT_OK -> showSnackbarMessage(R.string.successfully_added_task_message)
            DELETE_RESULT_OK -> showSnackbarMessage(R.string.successfully_deleted_task_message)
        }

        resultMessageShown = true
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun filterTasks(taskResult: Result<List<Task>>): LiveData<List<Task>> {
        val result = MutableLiveData<List<Task>>()

        if (taskResult is Success) {
            isDataLoadingError.value = false
            viewModelScope.launch {
                result.value = filterItems(taskResult.data, currentFiltering)
            }
        } else {
            result.value = emptyList()
            showSnackbarMessage(R.string.loading_tasks_error)
            isDataLoadingError.value = true
        }

        return result

    }

    private fun filterItems(tasks: List<Task>, filterType: TasksFilterType): List<Task> {
        val tasksToShow = ArrayList<Task>()

        //tasks are filtered based on the requestType
        for (task in tasks) {
            when (filterType) {
                TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                    tasksToShow.add(task)
                }
                TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                    tasksToShow.add(task)
                }
            }
        }

        return tasksToShow
    }

    fun refresh() {
        _forceUpdate.value = true
    }
}

@Suppress("unchecked_cast")
class TasksViewModelFactory(
    private val tasksRepository: TasksRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TasksViewModel(tasksRepository) as T
    }
}