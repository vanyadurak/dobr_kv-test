package efr.iv.igr.dto

import efr.iv.igr.model.Task
import efr.iv.igr.model.TaskStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateTaskRequest(
    @field:NotBlank(message = "The title cannot be empty.")
    @field:Size(min = 3, max = 100, message = "The title must between 3 and 100 characters.")
    val title: String,
    val description: String?
) {
    val getDescription: String get() = description ?: ""
}

fun CreateTaskRequest.toEntity(): Task {
    val dateTimeForCreateAndUpdate = LocalDateTime.now()
    val taskStatus = TaskStatus.NEW
    return Task(
        title = title,
        description = getDescription,
        status = taskStatus,
        createdAt = dateTimeForCreateAndUpdate,
        updatedAt = dateTimeForCreateAndUpdate
    )
}
