package me.alberto.notethat.data.source.local

import androidx.lifecycle.LiveData
import androidx.room.*
import me.alberto.notethat.data.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks_table")
    fun observeTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks_table WHERE entryid =:taskId ")
    fun observeTaskById(taskId: String): LiveData<Task>

    @Query("SELECT * FROM tasks_table")
    suspend fun getTasks(): List<Task>

    @Query("SELECT * FROM tasks_table WHERE entryid =:taskId")
    suspend fun getTaskById(taskId: String): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Update
    suspend fun updateTask(task: Task): Int

    @Query("UPDATE tasks_table SET completed =:completed WHERE entryid =:taskId")
    suspend fun updateCompleted(taskId: String, completed: Boolean)


    @Query("DELETE FROM tasks_table WHERE entryid =:taskId")
    suspend fun deleteTaskById(taskId: String): Int

    //Delete all tasks
    @Query("DELETE FROM tasks_table")
    suspend fun deleteTasks()

    @Query("DELETE FROM tasks_table WHERE completed=1")
    suspend fun deleteCompletedTasks(): Int
}