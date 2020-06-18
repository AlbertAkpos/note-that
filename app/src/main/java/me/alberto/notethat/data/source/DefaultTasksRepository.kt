package me.alberto.notethat.data.source

import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import me.alberto.notethat.data.Result
import me.alberto.notethat.data.Result.Success
import me.alberto.notethat.data.Task

class DefaultTasksRepository(
    private val taskRemoteDataSource: TaskDataSource,
    private val taskLocalDataSource: TaskDataSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : TasksRepository {


    override suspend fun getTasks(forceUpdate: Boolean): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTaskFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }

        return taskLocalDataSource.getTasks()
    }

    override suspend fun refreshTasks() {
        updateTaskFromRemoteDataSource()
    }

    override fun observeTasks(): LiveData<Result<List<Task>>> {
        return taskLocalDataSource.observeTasks()
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        return taskLocalDataSource.observeTask(taskId)
    }

    override suspend fun refreshTasks(taskId: String) {
        updateTaskFromRemoteDataSource(taskId)
    }

    private suspend fun updateTaskFromRemoteDataSource() {
        val remoteTasks = taskRemoteDataSource.getTasks()

        if (remoteTasks is Success) {
            taskLocalDataSource.deleteAllTasks()
            remoteTasks.data.forEach {
                taskLocalDataSource.saveTask(it)
            }
        } else if (remoteTasks is Result.Error) {
            throw remoteTasks.exception
        }
    }

    private suspend fun updateTaskFromRemoteDataSource(taskId: String) {
        val remoteTask = taskRemoteDataSource.getTask(taskId)

        if (remoteTask is Success) {
            taskLocalDataSource.saveTask(remoteTask.data)
        }
    }


    override suspend fun getTask(taskId: String, forceUpdate: Boolean): Result<Task> {
        if (forceUpdate) {
            updateTaskFromRemoteDataSource(taskId)
        }
        return taskLocalDataSource.getTask(taskId)
    }

    override suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { taskRemoteDataSource.saveTask(task) }
            launch { taskLocalDataSource.saveTask(task) }
        }
    }

    override suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { taskRemoteDataSource.completeTask(task) }
            launch { taskLocalDataSource.completeTask(task) }
        }
    }

    override suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let {
                completeTask(it.data)
            }
        }
    }

    override suspend fun activateTask(task: Task) {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskLocalDataSource.activateTask(task) }
                launch { taskRemoteDataSource.activateTask(task) }
            }
        }
    }

    override suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let {
                activateTask(it.data)
            }
        }
    }

    override suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { taskRemoteDataSource.clearCompletedTasks() }
            launch { taskLocalDataSource.clearCompletedTasks() }
        }
    }

    override suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.deleteAllTasks() }
                launch { taskLocalDataSource.deleteAllTasks() }
            }
        }
    }

    override suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { taskLocalDataSource.deleteTask(taskId) }
            launch { taskRemoteDataSource.deleteTask(taskId) }
        }
    }

    private suspend fun getTaskWithId(taskId: String): Result<Task> {
        return taskLocalDataSource.getTask(taskId)
    }


}