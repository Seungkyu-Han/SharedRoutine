package CoBo.SharedRoutine.global.config.response

import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime

class CoBoResponse<T>(
    private val code: Int,
    private val message: String,
    private val time: LocalDateTime,
    private val data:T?
) {

    constructor(coBoResponseStatus: CoBoResponseStatus):
            this(code = coBoResponseStatus.code,
                message = coBoResponseStatus.message,
                time = LocalDateTime.now(),
                data = null)

    constructor(data: T, coBoResponseStatus: CoBoResponseStatus):
            this(code = coBoResponseStatus.code,
                message = coBoResponseStatus.message,
                time = LocalDateTime.now(),
                data = data)

    fun getResponseEntity(): ResponseEntity<CoBoResponse<T>> {
        val httpStatusCode = HttpStatusCode.valueOf(code / 10)
        return ResponseEntity(this, httpStatusCode)
    }
}