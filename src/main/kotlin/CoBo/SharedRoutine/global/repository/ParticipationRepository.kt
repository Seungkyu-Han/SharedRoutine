package CoBo.SharedRoutine.global.repository

import CoBo.SharedRoutine.global.data.entity.Participation
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.data.entity.User
import CoBo.SharedRoutine.global.repository.Custom.ParticipationRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ParticipationRepository: JpaRepository<Participation, Int>, ParticipationRepositoryCustom{

    fun existsByUserAndRoutine(user: User, routine: Routine): Boolean
    fun findByUserAndRoutine(user: User, routine: Routine): Optional<Participation>
    fun findAllByUser(user: User): List<Participation>
    fun findAllByRoutine(routine: Routine): List<Participation>
}