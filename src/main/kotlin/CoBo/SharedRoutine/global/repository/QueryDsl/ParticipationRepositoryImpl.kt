package CoBo.SharedRoutine.global.repository.QueryDsl

import CoBo.SharedRoutine.global.data.entity.QParticipation.participation
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.data.entity.User
import CoBo.SharedRoutine.global.repository.Custom.ParticipationRepositoryCustom
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
class ParticipationRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory): ParticipationRepositoryCustom{

    @Transactional
    override fun updateGoalDate(goalDate: LocalDate, user: User, routine: Routine): Long {
        return jpaQueryFactory.update(participation)
            .set(participation.goalDate, goalDate)
            .where(participation.user.eq(user), participation.routine.eq(routine))
            .execute()
    }
}