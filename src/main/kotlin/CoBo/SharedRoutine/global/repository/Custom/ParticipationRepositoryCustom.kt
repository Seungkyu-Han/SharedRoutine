package CoBo.SharedRoutine.global.repository.Custom

import java.time.LocalDate

interface ParticipationRepositoryCustom {

    fun insertParticipation(userId: Int, routineId: Int, goalDate: LocalDate, week: Int)

    fun updateGoalDate(userId: Int, routineId: Int, goalDate: LocalDate): Int
}