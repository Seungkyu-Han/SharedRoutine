package CoBo.SharedRoutine.domain.routine.presentation

import CoBo.SharedRoutine.domain.routine.application.RoutineService
import CoBo.SharedRoutine.domain.routine.data.dto.RoutinePostReq
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/routine")
@Tag(name = "루틴")
class RoutineController (
    private val routineService: RoutineService
){

    @PostMapping
    @Operation(summary = "루틴 등록 API")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content())),
        ApiResponse(responseCode = "403", description = "인증 실패", content = arrayOf(Content()))
    )
    fun post(@RequestBody routinePostReq: RoutinePostReq, @Parameter(hidden = true) @RequestHeader("Authorization") authorization: String): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        return routineService.post(routinePostReq, authorization)
    }
}