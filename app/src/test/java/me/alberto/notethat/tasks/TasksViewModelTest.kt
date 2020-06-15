package me.alberto.notethat.tasks

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.alberto.notethat.data.Task
import me.alberto.notethat.data.source.FakeTestRepository
import me.alberto.notethat.getOrAwaitValue
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(maxSdk = Build.VERSION_CODES.P)
class TasksViewModelTest {

    private lateinit var viewModel: TasksViewModel

    private lateinit var taskRepository: FakeTestRepository

    @Before
    fun setup() {

        taskRepository = FakeTestRepository()
        val task1 = Task("Title1", "Description1")
        val task2 = Task("Title2", "Description2", true)
        val task3 = Task("Title3", "Description3", true)
        taskRepository.addTask(task1, task2, task3)

        viewModel = TasksViewModel(taskRepository)
    }

    @get:Rule
    var instantExcecutorRule = InstantTaskExecutorRule()

    @Test
    fun addNewTask_setNewTaskEvent() {
        //Given a fresh TaskViewModel
        //When adding a new task
        viewModel.addNewTask()
        val value = viewModel.newTaskEvent.getOrAwaitValue()
        //Then the new task is triggered
        assertThat(value.getContentIfNotHandled(), (not(nullValue())))
    }
}