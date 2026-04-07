package efr.iv.igr.model

import java.time.LocalDateTime

data class Task(
    var id: Long? = null,
    var title: String,
    var description: String,
    var status: TaskStatus,
    var createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
)
