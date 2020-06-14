package me.alberto.notethat.data.source

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.room.Room
import kotlinx.coroutines.*
import me.alberto.notethat.data.Result
import me.alberto.notethat.data.Result.Success
import me.alberto.notethat.data.Task
import me.alberto.notethat.data.source.local.TasksLocalDataSource
import me.alberto.notethat.data.source.local.ToDoDatabase
import me.alberto.notethat.data.source.remote.TaskRemoteDataSource

class DefaultTasksRepository private constructor(application: Application) {

    private val taskRemoteDataSource: TaskDataSource
    private val taskLocalDataSource: TaskDataSource
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    companion object {
        @Volatile
        private var INSTANCE: DefaultTasksRepository? = null

        fun getRepository(app: Application): DefaultTasksRepository {
            return INSTANCE ?: synchronized(this) {
                DefaultTasksRepository(app).also {
                    INSTANCE = it
                }
            }
        }
    }


    init {
        val database = Room.databaseBuilder(
            application.applicationContext,
            ToDoDatabase::class.java,
            "Tasks.db"
        ).build()

        taskRemoteDataSource = TaskRemoteDataSource
        taskLocalDataSource = TasksLocalDataSource(database.taskDao())
    }

    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>> {
        if (forceUpdate) {
            try {
                updateTaskFromRemoteDataSource()
            } catch (ex: Exception) {
                return Result.Error(ex)
            }
        }

        return taskLocalDataSource.getTasks()
    }

    suspend fun refreshTasks() {
        updateTaskFromRemoteDataSource()
    }

    fun observeTasks(): LiveData<Result<List<Task>>> {
        return taskLocalDataSource.observeTasks()
    }

    fun observeTask(taskId: String): LiveData<Result<Task>> {
        return taskLocalDataSource.observeTask(taskId)
    }

    suspend fun refreshTasks(taskId: String) {
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


    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task> {
        if (forceUpdate) {
            updateTaskFromRemoteDataSource(taskId)
        }
        return taskLocalDataSource.getTask(taskId)
    }

    suspend fun saveTask(task: Task) {
        coroutineScope {
            launch { taskRemoteDataSource.saveTask(task) }
            launch { taskLocalDataSource.saveTask(task) }
        }
    }

    suspend fun completeTask(task: Task) {
        coroutineScope {
            launch { taskRemoteDataSource.completeTask(task) }
            launch { taskLocalDataSource.completeTask(task) }
        }
    }

    suspend fun completeTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let {
                completeTask(it.data)
            }
        }
    }

    suspend fun activateTask(task: Task) = withContext(ioDispatcher) {
        coroutineScope {
            launch { taskLocalDataSource.activateTask(task) }
            launch { taskRemoteDataSource.activateTask(task) }
        }
    }

    suspend fun activateTask(taskId: String) {
        withContext(ioDispatcher) {
            (getTaskWithId(taskId) as? Success)?.let {
                activateTask(it.data)
            }
        }
    }

    suspend fun clearCompletedTasks() {
        coroutineScope {
            launch { taskRemoteDataSource.clearCompletedTasks() }
            launch { taskLocalDataSource.clearCompletedTasks() }
        }
    }

    suspend fun deleteAllTasks() {
        withContext(ioDispatcher) {
            coroutineScope {
                launch { taskRemoteDataSource.deleteAllTasks() }
                launch { taskLocalDataSource.deleteAllTasks() }
            }
        }
    }

    suspend fun deleteTask(taskId: String) {
        coroutineScope {
            launch { taskLocalDataSource.deleteTask(taskId) }
            launch { taskRemoteDataSource.deleteTask(taskId) }
        }
    }

    private suspend fun getTaskWithId(taskId: String): Result<Task> {
        return taskLocalDataSource.getTask(taskId)
    }


}