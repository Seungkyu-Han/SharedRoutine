package CoBo.SharedRoutine.domain.routine.Data.Dto.Res

data class RoutineGetParticipationElementRes(
    val routineId: Int?,
    val title: String,
    val achievementRate: Int,
    val checked: Boolean,
    val weekBit: String
)
