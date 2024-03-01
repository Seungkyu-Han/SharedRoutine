package CoBo.SharedRoutine.global.repository

import CoBo.SharedRoutine.global.data.entity.Participation
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.data.entity.User
import CoBo.SharedRoutine.global.repository.Custom.ParticipationRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParticipationRepository: JpaRepository<Participation, Int>, ParticipationRepositoryCustom{

    fun existsByUserAndRoutine(user: User, routine: Routine): Boolean
}