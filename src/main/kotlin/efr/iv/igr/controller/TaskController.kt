package efr.iv.igr.controller

import efr.iv.igr.dto.*
import efr.iv.igr.model.TaskStatus
import efr.iv.igr.service.TaskService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskService: TaskService) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createTask(@Valid @RequestBody createTaskRequest: CreateTaskRequest): Mono<CreatedTaskResponse> {
        return taskService.createTask(createTaskRequest)
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getTasks(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @RequestParam(required = false) status: TaskStatus?
    ): Mono<PageSearchTaskResponse<InformationTaskResponse>> {
        return taskService.getTasks(page, size, status)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getTask(@PathVariable id: Long): Mono<InformationTaskResponse> {
        return taskService.getTaskById(id)
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    fun updateTaskStatus(
        @PathVariable id: Long,
        @RequestBody updateTaskStatusRequest: UpdateTaskStatusRequest
    ): Mono<InformationTaskResponse> {
        return taskService.updateStatus(id, updateTaskStatusRequest)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTask(@PathVariable id: Long): Mono<Void> {
        return taskService.deleteTask(id)
    }
}