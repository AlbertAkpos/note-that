package me.alberto.notethat.data.source

import androidx.lifecycle.LiveData
import me.alberto.notethat.data.Result
import me.alberto.notethat.data.Result.Error
import me.alberto.notethat.data.Result.Success
import me.alberto.notethat.data.Task

class FakeDataSource(private val tasks: MutableList<Task>? = mutableListOf()) : TaskDataSource {
    override fun observeTasks(): LiveData<Result<List<Task>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTasks(): Result<List<Task>> {
        tasks?.let { return Success(ArrayList(it)) }
        return Error(Exception("Tasks not found"))
    }

    override suspend fun deleteAllTasks() {
        tasks?.clear()
    }

    override suspend fun saveTask(task: Task) {
        tasks?.add(task)
    }

    override suspend fun refreshTasks() {
        TODO("Not yet implemented")
    }

    override fun observeTask(taskId: String): LiveData<Result<Task>> {
        TODO("Not yet implemented")
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshTask(taskId: String) {
        TODO("Not yet implemented")
    }


    override suspend fun completeTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun completeTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(task: Task) {
        TODO("Not yet implemented")
    }

    override suspend fun activateTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun clearCompletedTasks() {
        TODO("Not yet implemented")
    }


    override suspend fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }
}