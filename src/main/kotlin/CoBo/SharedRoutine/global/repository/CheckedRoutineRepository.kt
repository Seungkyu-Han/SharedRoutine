package CoBo.SharedRoutine.global.repository

import CoBo.SharedRoutine.global.data.entity.CheckedRoutine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CheckedRoutineRepository: JpaRepository<CheckedRoutine, Int> {
}