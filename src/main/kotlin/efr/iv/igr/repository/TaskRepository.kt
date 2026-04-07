package efr.iv.igr.repository

import efr.iv.igr.dto.PageSearchTaskRequest
import efr.iv.igr.model.Task
import efr.iv.igr.model.TaskStatus
import java.time.LocalDateTime

interface TaskRepository {
    fun save(task: Task): Task
    fun findAll(pageSearchTaskRequest: PageSearchTaskRequest): List<Task>
    fun findById(id: Long): Task?
    fun updateStatus(id: Long, updateDateTime: LocalDateTime, status: TaskStatus): Boolean
    fun deleteById(id: Long): Boolean
    fun count(status: TaskStatus?): Long
}