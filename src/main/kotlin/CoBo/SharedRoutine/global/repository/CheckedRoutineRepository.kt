package CoBo.SharedRoutine.global.repository

import CoBo.SharedRoutine.global.data.entity.CheckedRoutine
import CoBo.SharedRoutine.global.data.entity.Participation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface CheckedRoutineRepository: JpaRepository<CheckedRoutine, Int> {

    fun existsByDateAndParticipation(date: LocalDate, participation: Participation): Boolean
}