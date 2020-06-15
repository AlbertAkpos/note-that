package me.alberto.notethat.data.source

import androidx.lifecycle.LiveData
import kotlinx.coroutines.Job
import me.alberto.notethat.data.Result
import me.alberto.notethat.data.Task

interface TasksRepository {
    suspend fun getTasks(forceUpdate: Boolean = false): Result<List<Task>>

    suspend fun refreshTasks()
    fun observeTasks(): LiveData<Result<List<Task>>>
    fun observeTask(taskId: String): LiveData<Result<Task>>

    suspend fun refreshTasks(taskId: String)

    suspend fun getTask(taskId: String, forceUpdate: Boolean = false): Result<Task>

    suspend fun saveTask(task: Task)

    suspend fun completeTask(task: Task)

    suspend fun completeTask(taskId: String)

    suspend fun activateTask(task: Task): Job

    suspend fun activateTask(taskId: String)

    suspend fun clearCompletedTasks()

    suspend fun deleteAllTasks()

    suspend fun deleteTask(taskId: String)
}