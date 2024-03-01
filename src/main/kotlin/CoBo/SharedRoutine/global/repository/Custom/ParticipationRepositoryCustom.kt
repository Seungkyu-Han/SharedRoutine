package CoBo.SharedRoutine.global.repository.Custom

import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.data.entity.User
import java.time.LocalDate

interface ParticipationRepositoryCustom {

    fun updateGoalDate(goalDate: LocalDate, user: User, routine: Routine): Long
}