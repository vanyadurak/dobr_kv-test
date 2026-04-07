package efr.iv.igr.dto

import efr.iv.igr.model.Task
import efr.iv.igr.model.TaskStatus
import java.time.LocalDateTime

data class CreatedTaskResponse(
    val id: Long,
    val title: String,
    val description: String,
    val status: TaskStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)

fun Task.createdTaskResponse(): CreatedTaskResponse {
    return CreatedTaskResponse(
        id = this.id ?: throw IllegalStateException("The ID cannot be empty."),
        title = this.title,
        description = this.description,
        status = this.status,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}
