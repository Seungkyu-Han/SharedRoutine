package CoBo.SharedRoutine.global.repository.Custom

interface ParticipationRepositoryCustom {

    fun existsByUserIdAndRoutineIdByQueryDsl(userId: Int, routineId: Int):Boolean
}