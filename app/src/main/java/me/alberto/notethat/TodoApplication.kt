package me.alberto.notethat

import android.app.Application
import me.alberto.notethat.data.source.TasksRepository
import timber.log.Timber
import timber.log.Timber.DebugTree

class TodoApplication : Application() {
    val tasksRepository: TasksRepository
        get() = ServiceLocator.provideTaskRepository(this)

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(DebugTree())
    }
}