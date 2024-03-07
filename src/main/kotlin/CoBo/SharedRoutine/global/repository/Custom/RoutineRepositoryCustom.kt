package CoBo.SharedRoutine.global.repository.Custom

import CoBo.SharedRoutine.global.data.entity.Routine

interface RoutineRepositoryCustom {
    fun findTopTen(): List<Routine>
}