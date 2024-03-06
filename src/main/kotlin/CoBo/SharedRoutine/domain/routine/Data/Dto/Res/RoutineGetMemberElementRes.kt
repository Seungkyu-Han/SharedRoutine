package CoBo.SharedRoutine.domain.routine.Data.Dto.Res

data class RoutineGetMemberElementRes(
    val id: Int,
    val name: String?,
    val image: String?,
    val achievementRate: Int
)
