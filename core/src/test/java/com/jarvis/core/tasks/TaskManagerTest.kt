package com.jarvis.core.tasks

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TaskManagerTest {

    private val manager = InMemoryTaskManager()

    @Test
    fun `createTask assigns id and persists`() = runTest {
        val task = Task(id = "", title = "Test task", goal = "Do something")
        val created = manager.createTask(task)
        assertTrue(created.id.isNotBlank())
        val fetched = manager.getTask(created.id)
        assertTrue(fetched != null)
        assertEquals("Test task", fetched?.title)
    }

    @Test
    fun `cancelTask sets status to CANCELLED`() = runTest {
        val task = manager.createTask(Task(id = "", title = "Cancel test", goal = ""))
        manager.cancelTask(task.id)
        val fetched = manager.getTask(task.id)
        assertEquals(TaskStatus.CANCELLED, fetched?.status)
    }
}
