package me.alberto.notethat.data.source.local

import androidx.room.RoomDatabase

abstract class ToDoDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

}