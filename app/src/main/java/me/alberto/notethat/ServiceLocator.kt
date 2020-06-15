package me.alberto.notethat

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import kotlinx.coroutines.runBlocking
import me.alberto.notethat.data.source.DefaultTasksRepository
import me.alberto.notethat.data.source.TaskDataSource
import me.alberto.notethat.data.source.TasksRepository
import me.alberto.notethat.data.source.local.TasksLocalDataSource
import me.alberto.notethat.data.source.local.ToDoDatabase
import me.alberto.notethat.data.source.remote.TaskRemoteDataSource

object ServiceLocator {

    private var database: ToDoDatabase? = null

    @Volatile
    private var tasksRepository: TasksRepository? = null
        @VisibleForTesting set

    fun provideTaskRepository(context: Context): TasksRepository {
        synchronized(this) {
            return tasksRepository ?: createTasksRepository(context)
        }
    }

    private fun createTasksRepository(context: Context): TasksRepository {
        val newRepository =
            DefaultTasksRepository(TaskRemoteDataSource, createTaskLocalDataSource(context))
        tasksRepository = newRepository
        return newRepository
    }

    private fun createTaskLocalDataSource(context: Context): TaskDataSource {
        val database = database ?: createDatabase(context)
        return TasksLocalDataSource(database.taskDao())
    }

    private fun createDatabase(context: Context): ToDoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            ToDoDatabase::class.java,
            "task_db"
        ).build()

        database = result
        return result
    }


    private val lock = Any()

    @VisibleForTesting
    fun resetRepository() {
        synchronized(lock) {
            runBlocking {
                TaskRemoteDataSource.deleteAllTasks()
            }

            //clear all data to avoid test pollution
            database?.apply {
                clearAllTables()
                close()
            }
            database = null
            tasksRepository = null
        }
    }


}