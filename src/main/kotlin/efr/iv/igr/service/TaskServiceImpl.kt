package efr.iv.igr.service

import efr.iv.igr.dto.*
import efr.iv.igr.exception.TaskNotFoundException
import efr.iv.igr.model.TaskStatus
import efr.iv.igr.repository.TaskRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Scheduler
import java.time.LocalDateTime
import kotlin.math.ceil

@Service
class TaskServiceImpl(
    private val taskRepository: TaskRepository,
    private val scheduler: Scheduler
) : TaskService {
    override fun createTask(createTaskRequest: CreateTaskRequest): Mono<CreatedTaskResponse> {
        return Mono.fromCallable {
            val entity = createTaskRequest.toEntity()
            taskRepository.save(entity)
        }
            .subscribeOn(scheduler)
            .map { it.createdTaskResponse() }
    }

    override fun getTaskById(id: Long): Mono<InformationTaskResponse> {
        return Mono.fromCallable {
            val task = taskRepository.findById(id)
            task ?: throw TaskNotFoundException("Task with ID $id not found.")
        }
            .subscribeOn(scheduler)
            .map { it.informationTaskResponse() }
    }

    override fun getTasks(
        page: Int,
        size: Int,
        status: TaskStatus?
    ): Mono<PageSearchTaskResponse<InformationTaskResponse>> {
        val request = PageSearchTaskRequest(page, size, status)

        return Mono.fromCallable {
            val total = taskRepository.count(status)
            val tasks = taskRepository.findAll(request).map { it.informationTaskResponse() }

            PageSearchTaskResponse(
                content = tasks,
                page = page,
                size = size,
                totalElements = total,
                totalPages = ceil(total.toDouble() / tasks.size).toInt(),
            )
        }.subscribeOn(scheduler)
    }

    override fun updateStatus(
        id: Long,
        updateTaskStatusRequest: UpdateTaskStatusRequest
    ): Mono<InformationTaskResponse> {
        return Mono.fromCallable {
            val updateDateTime = LocalDateTime.now()
            taskRepository.takeIf { it.updateStatus(id, updateDateTime, updateTaskStatusRequest.status) }
                ?.findById(id)
                ?: throw TaskNotFoundException("Task with ID $id not found.")
        }
            .subscribeOn(scheduler)
            .map { it.informationTaskResponse() }
    }

    override fun deleteTask(id: Long): Mono<Void> {
        return Mono.fromCallable {
            taskRepository.deleteById(id)
        }
            .subscribeOn(scheduler)
            .flatMap { deleted ->
                if (deleted) Mono.empty()
                else Mono.error { TaskNotFoundException("Task with ID $id not found.") }
            }
    }
}