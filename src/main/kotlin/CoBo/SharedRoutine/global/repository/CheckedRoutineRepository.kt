package CoBo.SharedRoutine.global.repository

import CoBo.SharedRoutine.global.data.entity.CheckedRoutine
import CoBo.SharedRoutine.global.data.entity.Participation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Optional

@Repository
interface CheckedRoutineRepository: JpaRepository<CheckedRoutine, Int> {

    fun existsByDateAndParticipation(date: LocalDate, participation: Participation): Boolean
    fun findByDateAndParticipation(date: LocalDate, participation: Participation): Optional<CheckedRoutine>
    fun findTopByParticipationOrderByDateDesc(participation: Participation): Optional<CheckedRoutine>
}