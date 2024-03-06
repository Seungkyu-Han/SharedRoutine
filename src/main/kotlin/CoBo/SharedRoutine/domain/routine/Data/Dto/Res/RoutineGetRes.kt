package CoBo.SharedRoutine.domain.routine.Data.Dto.Res

data class RoutineGetRes(
    val title: String,
    val description: String,
    val joined: Boolean,
    val memberList: ArrayList<RoutineGetMemberElementRes>
)
