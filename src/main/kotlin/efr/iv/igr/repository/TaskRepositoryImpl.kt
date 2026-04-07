package efr.iv.igr.repository

import efr.iv.igr.dto.PageSearchTaskRequest
import efr.iv.igr.model.Task
import efr.iv.igr.model.TaskStatus
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class TaskRepositoryImpl(
    private val jdbcClient: JdbcClient
) : TaskRepository {
    private val saveTaskSQL =
        """INSERT INTO tasks (title, description, status, created_at, updated_at) 
                VALUES (:title, :description, :status, :createdAt, :updatedAt)
        """

    private val findByIdSQL =
        """
            SELECT * FROM tasks
                WHERE id = :id
        """

    private val updateTaskSQL =
        """
           UPDATE tasks
                SET status = :status, updated_at = :updatedAt
                WHERE id = :id
        """

    private val deleteTaskSQL =
        """
            DELETE FROM tasks
                WHERE id = :id
        """

    private val findAllSQL =
        """
           SELECT * FROM tasks
                WHERE (:status IS NULL OR status = :status)
                ORDER BY created_at DESC
                LIMIT :limit
                OFFSET :offset
        """

    private val countSQL =
        """
            SELECT COUNT(*) FROM tasks 
                WHERE (:status IS NULL OR status = :status)
        """

    private val taskRowMapper = RowMapper { rs, _ ->
        Task(
            id = rs.getLong("id"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            status = TaskStatus.valueOf(rs.getString("status")),
            createdAt = rs.getTimestamp("created_at").toLocalDateTime(),
            updatedAt = rs.getTimestamp("updated_at").toLocalDateTime()
        )
    }

    override fun save(task: Task): Task {
        val keyHolder = GeneratedKeyHolder()

        jdbcClient.sql(saveTaskSQL)
            .param("title", task.title)
            .param("description", task.description)
            .param("status", task.status.name)
            .param("createdAt", task.createdAt)
            .param("updatedAt", task.updatedAt)
            .update(keyHolder, "id")

        val generatedId =
            keyHolder.key?.toLong() ?: throw IllegalArgumentException("The database did not generate the ID.")

        return task.copy(id = generatedId)
    }

    override fun findAll(pageSearchTaskRequest: PageSearchTaskRequest): List<Task> {
        return jdbcClient.sql(findAllSQL)
            .param("status", pageSearchTaskRequest.status?.name)
            .param("limit", pageSearchTaskRequest.size)
            .param("offset", pageSearchTaskRequest.offset)
            .query(taskRowMapper)
            .list()
    }

    override fun count(status: TaskStatus?): Long {
        return jdbcClient.sql(countSQL)
            .param("status", status?.name)
            .query(Long::class.java)
            .single()
    }

    override fun findById(id: Long): Task? {
        return jdbcClient.sql(findByIdSQL)
            .param("id", id)
            .query(taskRowMapper)
            .optional()
            .orElse(null)
    }

    override fun updateStatus(id: Long, updateDateTime: LocalDateTime, status: TaskStatus): Boolean {
        return jdbcClient.sql(updateTaskSQL)
            .param("id", id)
            .param("status", status.name)
            .param("updatedAt", updateDateTime)
            .update() > 0
    }

    override fun deleteById(id: Long): Boolean {
        return jdbcClient.sql(deleteTaskSQL)
            .param("id", id)
            .update() > 0
    }
}