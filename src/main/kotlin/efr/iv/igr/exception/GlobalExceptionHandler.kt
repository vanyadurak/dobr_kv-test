package efr.iv.igr.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(TaskNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleTaskNotFoundException(ex: TaskNotFoundException): Mono<ErrorResponse> {
        return Mono.just(
            ErrorResponse(
                status = HttpStatus.NOT_FOUND.value(),
                code = "NOT_FOUND",
                message = ex.message
            )
        )
    }

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationException(e: WebExchangeBindException): Mono<ErrorResponse> {
        val errors = e.bindingResult.fieldErrors.associate { error ->
            error.field to (error.defaultMessage ?: "Invalid value.")
        }

        return Mono.just(
            ErrorResponse(
                status = HttpStatus.BAD_REQUEST.value(),
                code = "VALIDATION_ERROR",
                details = errors
            )
        )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): Mono<ErrorResponse> {
        return Mono.just(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                code = "INVALID_SERVER_ERROR",
                message = ex.message
            )
        )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(ex: Exception): Mono<ErrorResponse> {
        return Mono.just(
            ErrorResponse(
                status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error has occurred."
            )
        )
    }
}