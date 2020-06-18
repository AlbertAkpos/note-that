package me.alberto.notethat.taskdetails

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import me.alberto.notethat.R
import me.alberto.notethat.ServiceLocator
import me.alberto.notethat.ServiceLocator.resetRepository
import me.alberto.notethat.data.Task
import me.alberto.notethat.data.source.FakeAndroidTestRepository
import me.alberto.notethat.data.source.TasksRepository
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@MediumTest
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class TaskDetailFragmentTest {

    private lateinit var repository: TasksRepository

    @Before
    fun initRepository() {
        repository = FakeAndroidTestRepository()
        ServiceLocator.tasksRepository = repository
    }

    @After
    fun cleanUpDb() = runBlocking {
        resetRepository()
    }

    @Test
    fun activeTaskDetails_DisplayedInUi() {

        runBlockingTest {
            val activeTask = Task("Active task", "Active task desc", false)
            repository.saveTask(activeTask)
            val bundle = TaskDetailFragmentArgs(activeTask.id).toBundle()
            launchFragmentInContainer<TaskDetailFragment>(bundle, R.style.AppTheme)

            onView(withId(R.id.task_detail_title_text)).check(matches(isDisplayed()))
            onView(withId(R.id.task_detail_title_text)).check(matches(withText("Active task")))
            onView(withId(R.id.task_detail_description_text)).check(matches(isDisplayed()))
            onView(withId(R.id.task_detail_description_text)).check(matches(withText("Active task desc")))
            //To make sure active checkbox is shown as unchecked
            onView(withId(R.id.task_detail_complete_checkbox)).check(matches(isDisplayed()))
            onView(withId(R.id.task_detail_complete_checkbox)).check(matches(not(isChecked())))

            Thread.sleep(2_000)
        }

    }
}