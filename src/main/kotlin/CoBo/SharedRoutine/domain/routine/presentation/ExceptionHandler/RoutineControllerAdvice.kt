package CoBo.SharedRoutine.domain.routine.presentation.ExceptionHandler

import CoBo.SharedRoutine.domain.routine.presentation.RoutineController
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.NoSuchElementException

@RestControllerAdvice(basePackageClasses = [RoutineController::class])
class RoutineControllerAdvice {

    @ExceptionHandler(NoSuchElementException::class)
    fun noSuchElementExceptionHandler(noSuchElementException: NoSuchElementException): ResponseEntity<CoBoResponseDto<String?>> {
        return CoBoResponse(noSuchElementException.message, CoBoResponseStatus.NOT_FOUND_ELEMENT).getResponseEntity()
    }

    @ExceptionHandler(DuplicateKeyException::class)
    fun duplicateKeyExceptionHandler(duplicateKeyException: DuplicateKeyException): ResponseEntity<CoBoResponseDto<String?>> {
        return CoBoResponse(duplicateKeyException.message, CoBoResponseStatus.CONFLICT).getResponseEntity()
    }
}