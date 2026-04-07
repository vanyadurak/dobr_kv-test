package efr.iv.igr.controller

import efr.iv.igr.dto.*
import efr.iv.igr.exception.GlobalExceptionHandler
import efr.iv.igr.exception.TaskNotFoundException
import efr.iv.igr.model.TaskStatus
import efr.iv.igr.service.TaskService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.util.stream.Stream

@ExtendWith(MockitoExtension::class)
class TaskControllerTest {

    @Mock
    lateinit var service: TaskService

    @InjectMocks
    lateinit var controller: TaskController

    lateinit var client: WebTestClient

    @BeforeEach
    fun setup() {
        client = WebTestClient
            .bindToController(controller)
            .controllerAdvice(GlobalExceptionHandler())
            .build()
    }

    companion object {
        @JvmStatic
        fun descriptionProvider(): Stream<String?> = Stream.of("Description", null)
    }

    @ParameterizedTest
    @MethodSource("descriptionProvider")
    fun `givenValidTask whenCreateTask thenReturnCreated`(desc: String?) {
        val response = mockCreatedTaskResponse(desc)

        whenever(service.createTask(any())).thenReturn(Mono.just(response))

        client.post()
            .uri("/api/tasks")
            .bodyValue(CreateTaskRequest("Title", desc))
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Title")
            .jsonPath("$.description").isEqualTo(desc ?: "")
    }

    @ParameterizedTest
    @ValueSource(strings = ["Ti", ""])
    fun `givenInvalidTask whenCreateTask thenReturnValidationError`(title: String) {
        client.post()
            .uri("/api/tasks")
            .bodyValue(CreateTaskRequest(title, "Description"))
            .exchange()
            .expectStatus().isBadRequest
    }

    @Test
    fun `givenExistIdTask whenGetTaskById thenReturnTask`() {
        val response = mockInformationTaskResponse(TaskStatus.NEW)
        whenever(service.getTaskById(1)).thenReturn(Mono.just(response))

        client.get()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.title").isEqualTo("Title")
            .jsonPath("$.description").isEqualTo("Description")
    }

    @Test
    fun `givenNotExistIdTask whenGetTaskById thenReturnNotFoundError`() {
        whenever(service.getTaskById(999))
            .thenReturn(Mono.error(TaskNotFoundException("Task with ID 999 not found.")))

        client.get()
            .uri("/api/tasks/999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Task with ID 999 not found.")
    }

    @Test
    fun `givenExistIdTask whenDeleteTaskById thenReturnNoContent`() {
        whenever(service.deleteTask(1)).thenReturn(Mono.empty())

        client.delete()
            .uri("/api/tasks/1")
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `givenNotExistIdTask whenDeleteTaskById thenReturnNotFoundError`() {
        whenever(service.deleteTask(999))
            .thenReturn(Mono.error(TaskNotFoundException("Task with ID 999 not found.")))

        client.delete()
            .uri("/api/tasks/999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Task with ID 999 not found.")
    }

    @Test
    fun `givenUpdateTaskStatusRequest whenUpdateTaskStatusRequest thenReturnUpdatedTask`() {
        val request = UpdateTaskStatusRequest(TaskStatus.DONE)
        val response = mockInformationTaskResponse(TaskStatus.DONE)

        whenever(service.updateStatus(1, request))
            .thenReturn(Mono.just(response))

        client.patch()
            .uri("/api/tasks/1/status")
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(1)
            .jsonPath("$.status").isEqualTo("DONE")
    }

    @Test
    fun `givenNotExistIdTask whenUpdateTaskStatus thenReturnNotFoundError`() {
        val request = UpdateTaskStatusRequest(TaskStatus.DONE)

        whenever(service.updateStatus(999, request))
            .thenReturn(Mono.error(TaskNotFoundException("Task with ID 999 not found.")))

        client.patch()
            .uri("/api/tasks/999/status")
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.message").isEqualTo("Task with ID 999 not found.")
    }

    @Test
    fun `givenPageForSearchTask whenGetTasks thenReturnPageTask`() {
        val page = 0
        val size = 10
        val status = TaskStatus.NEW

        val response = mockPageSearchTaskResponse(status)
        whenever(service.getTasks(page, size, status)).thenReturn(Mono.just(response))

        client.get()
            .uri { uriBuilder ->
                uriBuilder.path("/api/tasks")
                    .queryParam("page", page)
                    .queryParam("size", size)
                    .queryParam("status", status.name)
                    .build()
            }
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.page").isEqualTo(page)
            .jsonPath("$.size").isEqualTo(size)
            .jsonPath("$.totalElements").isEqualTo(response.totalElements)
            .jsonPath("$.totalPages").isEqualTo(response.totalPages)
            .jsonPath("$.content.length()").isEqualTo(response.content.size)
            .jsonPath("$.content[0].status").isEqualTo(status.name)
            .jsonPath("$.content[1].id").isEqualTo(2)
            .jsonPath("$.content[2].id").isEqualTo(3)
    }

    private fun mockCreatedTaskResponse(description: String?): CreatedTaskResponse = CreatedTaskResponse(
        id = 1,
        status = TaskStatus.NEW,
        title = "Title",
        description = description ?: "",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    private fun mockInformationTaskResponse(status: TaskStatus): InformationTaskResponse = InformationTaskResponse(
        id = 1,
        status = status,
        title = "Title",
        description = "Description",
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now(),
    )

    private fun mockPageSearchTaskResponse(status: TaskStatus? = TaskStatus.NEW): PageSearchTaskResponse<InformationTaskResponse> =
        PageSearchTaskResponse(
            content = listOf(
                mockInformationTaskResponse(status!!),
                mockInformationTaskResponse(status).copy(id = 2).copy(createdAt = LocalDateTime.now().plusDays(1)),
                mockInformationTaskResponse(status).copy(id = 3).copy(createdAt = LocalDateTime.now().plusDays(2)),
            ),
            page = 0,
            size = 10,
            totalElements = 1,
            totalPages = 1
        )
}