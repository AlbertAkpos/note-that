package me.alberto.notethat.data.source

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import me.alberto.notethat.data.Result.Success
import me.alberto.notethat.data.Task
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class DefaultTasksRepositoryTest {
    private val task1 = Task("Title1", "Description1")
    private val task2 = Task("Title2", "Description2")
    private val task3 = Task("Title3", "Description3")
    private val remoteTasks = listOf(task1, task2).sortedBy { it.id }
    private val localTasks = listOf(task3).sortedBy { it.id }
    private val newTasks = listOf(task3).sortedBy { it.id }

    private lateinit var tasksRemoteDataSource: FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource

    //class under test
    private lateinit var tasksRepository: DefaultTasksRepository

    @Before
    fun setup() {
        tasksRemoteDataSource = FakeDataSource(remoteTasks.toMutableList())
        tasksLocalDataSource = FakeDataSource(localTasks.toMutableList())

        tasksRepository = DefaultTasksRepository(
            tasksRemoteDataSource,
            tasksLocalDataSource,
            Dispatchers.Unconfined
        )

    }

    @Test
    fun getTasks_requestAllTasksFromRemoteDataSource() {
        runBlockingTest {
            val tasks = tasksRepository.getTasks(true) as Success
            assertThat(tasks.data, IsEqual(remoteTasks))
        }
    }
}