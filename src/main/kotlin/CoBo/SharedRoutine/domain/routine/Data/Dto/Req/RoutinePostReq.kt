package CoBo.SharedRoutine.domain.routine.Data.Dto.Req

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

data class RoutinePostReq(
    @Schema(description = "루틴 제목", example = "물 마시기")
    val title: String,
    @Schema(description = "루틴 상세 설명", example = "건강을 위해 물을 마시는 루틴입니다.")
    val description: String,
    @Schema(description = "방장 목표 날짜", example = "2024-08-15")
    val goalDate: LocalDate,
    @Schema(description = "방장 요일 반복", example = "1100100")
    val weekBit: String
)
