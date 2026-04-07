package efr.iv.igr.service

import efr.iv.igr.dto.*
import efr.iv.igr.model.TaskStatus
import reactor.core.publisher.Mono

interface TaskService {
    fun createTask(createTaskRequest: CreateTaskRequest): Mono<CreatedTaskResponse>
    fun getTaskById(id: Long): Mono<InformationTaskResponse>
    fun getTasks(page: Int, size: Int, status: TaskStatus?): Mono<PageSearchTaskResponse<InformationTaskResponse>>
    fun updateStatus(id: Long, updateTaskStatusRequest: UpdateTaskStatusRequest): Mono<InformationTaskResponse>
    fun deleteTask(id: Long): Mono<Void>
}