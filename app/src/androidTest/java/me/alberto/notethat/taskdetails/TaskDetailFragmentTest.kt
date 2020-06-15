package me.alberto.notethat.taskdetails

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import me.alberto.notethat.R
import me.alberto.notethat.data.Task
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {
    @Test
    fun activeTaskDetails_DisplatedInUi() {
        val activeTask = Task("Active task", "Active task desc", false)

        val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
        launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)
        Thread.sleep(2_000)
    }
}