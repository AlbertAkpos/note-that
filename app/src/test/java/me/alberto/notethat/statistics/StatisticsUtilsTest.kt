package me.alberto.notethat.statistics

import me.alberto.notethat.data.Task
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class StatisticsUtilsTest {

    @Test
    fun getActiveAndCompletedStates_noCompleted_returnsHundredZero() {
        //create an active task
        val tasks = listOf<Task>(
            Task("title", "desc", isCompleted = false)
        )

        //call function

        val result = getActiveAndCompletedStats(tasks)

        //check the result

        assertThat(result.completedTasksPercent, `is`(0f))
        assertThat(result.activeTasksPercent, `is`(100F))
    }
}