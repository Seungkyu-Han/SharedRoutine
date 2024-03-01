package CoBo.SharedRoutine.domain.routine.presentation

import CoBo.SharedRoutine.domain.routine.Data.Dto.RoutinePostParticipationReq
import CoBo.SharedRoutine.domain.routine.Data.Dto.RoutinePostReq
import CoBo.SharedRoutine.domain.routine.application.RoutineService
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

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
    fun post(@RequestBody routinePostReq: RoutinePostReq, @Parameter(hidden = true) authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        return routineService.post(routinePostReq, authentication)
    }

    @PostMapping("/participate")
    @Operation(summary = "루틴 참여 API")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content())),
        ApiResponse(responseCode = "403", description = "인증 실패", content = arrayOf(Content())),
        ApiResponse(responseCode = "404", description = "유효하지 않은 루틴", content = arrayOf(Content())),
        ApiResponse(responseCode = "400", description = "이미 참여한 루틴입니다.", content = arrayOf(Content()))
    )
    fun postParticipation(@RequestBody routinePostParticipationReq: RoutinePostParticipationReq,
                          @Parameter(hidden = true) authentication: Authentication)
            : ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        return routineService.postParticipation(routinePostParticipationReq, authentication)
    }
}