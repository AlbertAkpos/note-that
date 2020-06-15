package me.alberto.notethat.data.source

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import kotlinx.coroutines.runBlocking
import me.alberto.notethat.data.Result
import me.alberto.notethat.data.Result.*
import me.alberto.notethat.data.Task

class FakeAndroidTestRepository : TasksRepository {

    var taskServiceData: LinkedHashMap<String, Task> = LinkedHashMap()

    private var shouldReturnError = false

    private val observableTasks = MutableLiveData<Result<List<Task>>>()

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    override suspend fun refreshTasks() {
        observableTasks.value = getTasks()
    }

    override suspend fun refreshTasks(taskId: String) {
        refreshTasks()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        runBlocking { refreshTasks() }
        return observableTasks
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        runBlocking { refreshTasks() }
        return observableTasks.map { tasks ->
            when (tasks) {
                is Loading -> Loading
                is Error -> Error(tasks.exception)
                is Success -> {
                    val task = tasks.data.firstOrNull { it.id == taskId }
                        ?: return@map Error(Exception("Not found"))
                    Success(task)
                }
            }
        }
    }

    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (shouldReturnError) {
            return Error(Exception("Test exception"))
        }
        taskServiceData[taskId]?.let {
            return Success(it)
        }
        return Error(Exception("Could not find task"))
    }

    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (shouldReturnError) return Error(Exception("Test exception"))

        return Success(taskServiceData.values.toList())
    }


    override suspend fun saveTask(task: Task) {
        taskServiceData[task.id] = task
    }

    override suspend fun completeTask(task: Task) {
        val completedTask = Task(task.title, task.description, true, task.id)
        taskServiceData[task.id] = completedTask
    }

    override suspend fun completeTask(taskId: String) {
        throw NotImplementedError()
    }

    override suspend fun activateTask(task: Task) {
        val activeTask = Task(task.title, task.description, false, task.id)
        taskServiceData[task.id] = activeTask
    }

    override suspend fun activateTask(taskId: String) {
        throw NotImplementedError()
    }

    override suspend fun clearCompletedTasks() {
        taskServiceData = taskServiceData.filterValues {
            !it.isCompleted
        } as LinkedHashMap<String, Task>
    }

    override suspend fun deleteAllTasks() {
        taskServiceData.clear()
        refreshTasks()
    }

    override suspend fun deleteTask(taskId: String) {
        taskServiceData.remove(taskId)
        refreshTasks()
    }

    fun addTasks(vararg tasks: Task) {
        for (task in tasks) {
            taskServiceData[task.id] = task
        }
        runBlocking { refreshTasks() }
    }
}