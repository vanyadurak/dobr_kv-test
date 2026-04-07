package efr.iv.igr.dto

import efr.iv.igr.model.TaskStatus
import jakarta.validation.constraints.NotBlank

data class UpdateTaskStatusRequest(
    @field:NotBlank(message = "The status cannot be empty.")
    val status: TaskStatus
)
