package me.alberto.notethat.statistics

import me.alberto.notethat.data.Task

internal fun getActiveAndCompletedStats(tasks: List<Task>?): StatsResult {
    return if (tasks == null || tasks.isEmpty()) {
        StatsResult(0f, 0f)
    } else {
        val totalTasks = tasks.size
        val numberOfActiveTasks = tasks.count { it.isActive }
        StatsResult(
            activeTasksPercent = 100f * numberOfActiveTasks / tasks.size,
            completedTasksPercent = 100f * (totalTasks - numberOfActiveTasks) / tasks.size
        )
    }
}


data class StatsResult(
    val activeTasksPercent: Float,
    val completedTasksPercent: Float
)