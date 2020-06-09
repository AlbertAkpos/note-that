package me.alberto.notethat.data.source.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.alberto.notethat.data.Result
import me.alberto.notethat.data.Result.Error
import me.alberto.notethat.data.Result.Success
import me.alberto.notethat.data.Task
import me.alberto.notethat.data.source.TaskDataSource

class TasksLocalDataSource internal constructor(
    private val taskDoa: TaskDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TaskDataSource {
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return taskDoa.observeTasks().map {
            Success(it)
        }
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return taskDoa.observeTaskById(taskId).map { Success(it) }
    }

    override suspend fun getTasks(): Result<List<Task>> = withContext(ioDispatcher) {
        return@withContext try {
            Success(taskDoa.getTasks())
        } catch (e: Exception) {
            Error(e)
        }
    }

    override suspend fun refreshTasks() {
        //
    }


    override suspend fun getTask(taskId: String): Result<Task> = withContext(ioDispatcher) {
        try {
            val task = taskDoa.getTaskById(taskId)
            if (task != null) {
                return@withContext Success(task)
            } else {
                return@withContext Error(Exception("Task not found"))
            }
        } catch (e: Exception) {
            return@withContext Error(e)
        }
    }

    override suspend fun refreshTask(taskId: String) {
        //
    }

    override suspend fun saveTask(task: Task) = withContext(ioDispatcher) {
        taskDoa.insertTask(task)
    }

    override suspend fun completeTask(task: Task) = withContext(ioDispatcher) {
        taskDoa.updateCompleted(task.id, true)
    }

    override suspend fun completeTask(taskId: String) {
        taskDoa.updateCompleted(taskId, true)
    }

    override suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        taskDoa.updateCompleted(task.id, false)
    }

    override suspend fun activateTask(taskId: String) {
        taskDoa.updateCompleted(taskId, false)
    }

    override suspend fun clearCompletedTasks() = withContext<Unit>(ioDispatcher) {
        taskDoa.deleteCompletedTasks()
    }

    override suspend fun deleteAllTasks() = withContext(ioDispatcher) {
        taskDoa.deleteTasks()
    }

    override suspend fun deleteTask(taskId: String) = withContext<Unit>(ioDispatcher) {
        taskDoa.deleteTaskById(taskId)
    }
}