package CoBo.SharedRoutine.global.repository

import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.repository.Custom.RoutineRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoutineRepository: JpaRepository<Routine, Int>, RoutineRepositoryCustom {
}