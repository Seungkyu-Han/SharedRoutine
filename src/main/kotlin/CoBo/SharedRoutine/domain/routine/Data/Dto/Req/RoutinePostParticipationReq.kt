package CoBo.SharedRoutine.domain.routine.Data.Dto.Req

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class RoutinePostParticipationReq(
    @Schema(description = "루틴 아이디", example = "1")
    val routineId: Int,
    @Schema(description = "목표 날짜", example = "2024-08-15")
    val goalDate: LocalDate,
    @Schema(description = "요일 반복", example = "1100100")
    val weekBit: String
)