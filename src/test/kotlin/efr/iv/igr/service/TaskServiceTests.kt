package efr.iv.igr.service

import efr.iv.igr.dto.CreateTaskRequest
import efr.iv.igr.dto.PageSearchTaskRequest
import efr.iv.igr.dto.UpdateTaskStatusRequest
import efr.iv.igr.dto.toEntity
import efr.iv.igr.exception.TaskNotFoundException
import efr.iv.igr.model.TaskStatus
import efr.iv.igr.repository.TaskRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import reactor.core.scheduler.Schedulers
import reactor.test.StepVerifier
import java.time.LocalDateTime
import kotlin.math.ceil
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class TaskServiceTests {
    @Mock
    lateinit var taskRepository: TaskRepository

    lateinit var taskService: TaskServiceImpl

    @BeforeEach
    fun setup() {
        taskService = TaskServiceImpl(taskRepository, Schedulers.immediate())
    }

    @Test
    fun `givenValidCreateTaskRequest whenCreateTask thenReturnCreated`() {
        val req = mockCreateTaskRequest()
        whenever(taskRepository.save(any())).thenReturn(req.toEntity().copy(id = 1))
        val result = taskService.createTask(req)

        StepVerifier.create(result)
            .assertNext { response ->
                assert(response.id == 1L)
                assert(response.title == "Title")
                assert(response.description == "Description")
                assert(response.status == TaskStatus.NEW)
            }
            .verifyComplete()
    }

    @Test
    fun `givenExistIdTask whenGetTaskById thenReturnTask`() {
        val req = mockCreateTaskRequest()
        whenever(taskRepository.findById(any())).thenReturn(req.toEntity().copy(id = 1))
        val result = taskService.getTaskById(1L)

        StepVerifier.create(result)
            .assertNext { response ->
                assert(response.id == 1L)
                assert(response.title == "Title")
                assert(response.description == "Description")
                assert(response.status == TaskStatus.NEW)
            }
            .verifyComplete()
    }

    @Test
    fun `givenNotExistIdTask whenGetTaskById thenReturnTaskNotFoundException`() {
        whenever(taskRepository.findById(any()))
            .thenThrow(TaskNotFoundException("Task with ID 2 not found."))
        val result = taskService.getTaskById(2L)

        StepVerifier.create(result)
            .expectErrorSatisfies { ex ->
                assert(ex is TaskNotFoundException)
                assert((ex as TaskNotFoundException).message == "Task with ID 2 not found.")
            }
            .verify()
    }

    @Test
    fun `givenExistIdTask whenDeleteTaskById thenReturnNoContent`() {
        whenever(taskRepository.deleteById(1L)).thenReturn(true)
        val result = taskService.deleteTask(1L)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    fun `givenNotExistIdTask whenDeleteTaskById thenReturnTaskNotFoundException`() {
        whenever(taskRepository.deleteById(2L)).thenReturn(false)
        val result = taskService.deleteTask(2L)

        StepVerifier.create(result)
            .expectErrorSatisfies { ex ->
                assert(ex is TaskNotFoundException)
                assert((ex as TaskNotFoundException).message == "Task with ID 2 not found.")
            }
            .verify()
    }

    @Test
    fun `givenUpdateTaskStatusRequest whenUpdateTaskStatus thenReturnTask`() {
        val id = 1L
        val request = UpdateTaskStatusRequest(TaskStatus.IN_PROGRESS)
        whenever(
            taskRepository.updateStatus(
                eq(id),
                any(),
                eq(request.status)
            )
        ).thenReturn(true)
        whenever(taskRepository.findById(eq(id)))
            .thenReturn(mockCreateTaskRequest().toEntity().copy(id = id, status = request.status))
        val result = taskService.updateStatus(id, request)

        StepVerifier.create(result)
            .assertNext { response ->
                assert(response.id == id)
                assert(response.status == TaskStatus.IN_PROGRESS)
            }
            .verifyComplete()
    }

    @Test
    fun `givenUpdateTaskStatusRequest whenUpdateTaskStatus thenReturnTaskNotFoundException`() {
        val id = 1L
        val request = UpdateTaskStatusRequest(TaskStatus.IN_PROGRESS)
        whenever(
            taskRepository.updateStatus(
                eq(id),
                any(),
                eq(request.status)
            )
        ).thenReturn(false)
        val result = taskService.updateStatus(id, request)

        StepVerifier.create(result)
            .expectErrorSatisfies { ex ->
                assert(ex is TaskNotFoundException)
                assert((ex as TaskNotFoundException).message == "Task with ID 1 not found.")
            }
            .verify()
    }

    @Test
    fun `givenPageAndSizeAndStatus whenGetTasks thenReturnPagedTasks`() {
        val page = 0
        val size = 2
        val status = TaskStatus.NEW
        val tasksFromRepo = listOf(
            mockCreateTaskRequest().toEntity().copy(id = 1, status = status),
            mockCreateTaskRequest().toEntity().copy(id = 2, status = status)
                .copy(createdAt = LocalDateTime.now().plusDays(1))
        )
        whenever(taskRepository.count(status)).thenReturn(tasksFromRepo.size.toLong())
        whenever(taskRepository.findAll(PageSearchTaskRequest(page, size, status)))
            .thenReturn(tasksFromRepo)
        val result = taskService.getTasks(page, size, status)

        StepVerifier.create(result)
            .assertNext { response ->
                assert(response.page == page)
                assert(response.size == size)
                assert(response.totalElements == tasksFromRepo.size.toLong())
                assert(response.totalPages == ceil(tasksFromRepo.size.toDouble() / size).toInt())
                assert(response.content.size == tasksFromRepo.size)
                assert(response.content[0].id == 1L)
                assert(response.content[1].id == 2L)
            }
            .verifyComplete()
    }

    private fun mockCreateTaskRequest(): CreateTaskRequest = CreateTaskRequest(
        title = "Title",
        description = "Description"
    )
}