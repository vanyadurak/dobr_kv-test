package efr.iv.igr.repository

import efr.iv.igr.dto.PageSearchTaskRequest
import efr.iv.igr.model.Task
import efr.iv.igr.model.TaskStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@SpringBootTest
@ActiveProfiles("test")
class TaskRepositoryTests {

    @Autowired
    private lateinit var repository: TaskRepositoryImpl

    private lateinit var now: LocalDateTime

    @BeforeEach
    fun setup() {
        now = LocalDateTime.now()
    }

    @Test
    fun `givenNewTask whenSaveTask thenReturnTask`() {
        val task = Task(0L, "Test Task", "Desc", TaskStatus.NEW, now, now)
        val saved = repository.save(task)

        assertNotNull(saved.id)
        assertEquals("Test Task", saved.title)
    }

    @Test
    fun `givenExistingTaskId whenFindById thenReturnTask`() {
        val task = repository.save(Task(0L, "Task 1", "Desc", TaskStatus.NEW, now, now))
        val found = repository.findById(task.id!!)

        assertEquals(task.id, found?.id)
        assertEquals("Task 1", found?.title)
    }

    @Test
    fun `givenExistingTaskId whenUpdateStatus thenReturnUpdatedTask`() {
        val task = repository.save(Task(0L, "Task 2", "Desc", TaskStatus.NEW, now, now))
        val updated = repository.updateStatus(task.id!!, LocalDateTime.now(), TaskStatus.DONE)

        assertTrue(updated)
        val found = repository.findById(task.id!!)
        assertEquals(TaskStatus.DONE, found?.status)
    }

    @Test
    fun `givenExistingTaskId whenDeleteById thenReturnTrue`() {
        val task = repository.save(Task(0L, "Task 3", "Desc", TaskStatus.NEW, now, now))
        val deleted = repository.deleteById(task.id!!)

        assertTrue(deleted)
        val found = repository.findById(task.id!!)
        assertNull(found)
    }

    @Test
    fun `givenSearch whenFindAll thenReturnTasks`() {
        repository.save(Task(0L, "Task 1", "Desc", TaskStatus.NEW, now, now))
        repository.save(Task(0L, "Task 2", "Desc", TaskStatus.DONE, now, now))
        repository.save(Task(0L, "Task 3", "Desc", TaskStatus.NEW, now, now))

        val pageRequest = PageSearchTaskRequest(
            page = 0,
            size = 2,
            status = TaskStatus.NEW
        )
        val result = repository.findAll(pageRequest)

        assertEquals(2, result.size)
        assert(result.all { it.status == TaskStatus.NEW })
    }
}