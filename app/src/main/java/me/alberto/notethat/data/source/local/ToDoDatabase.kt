package me.alberto.notethat.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import me.alberto.notethat.data.Task

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class ToDoDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

}