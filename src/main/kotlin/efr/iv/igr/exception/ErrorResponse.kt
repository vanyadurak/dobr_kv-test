package efr.iv.igr.exception

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String? = null,
    val time: LocalDateTime = LocalDateTime.now(),
    var details: Map<String, String>? = null
)