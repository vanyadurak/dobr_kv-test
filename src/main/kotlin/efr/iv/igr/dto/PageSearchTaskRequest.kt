package efr.iv.igr.dto

import efr.iv.igr.model.TaskStatus

data class PageSearchTaskRequest(
    val page: Int,
    val size: Int,
    val status: TaskStatus? = null
) {
    val offset: Int get() = page * size
}