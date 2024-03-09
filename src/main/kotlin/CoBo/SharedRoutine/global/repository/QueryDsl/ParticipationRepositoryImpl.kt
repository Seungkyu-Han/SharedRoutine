package CoBo.SharedRoutine.global.repository.QueryDsl

import CoBo.SharedRoutine.global.data.entity.QParticipation.participation
import CoBo.SharedRoutine.global.repository.Custom.ParticipationRepositoryCustom
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ParticipationRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory): ParticipationRepositoryCustom {

    override fun existsByUserIdAndRoutineIdByQueryDsl(userId: Int, routineId: Int): Boolean {
        return jpaQueryFactory
            .select(participation)
            .from(participation)
            .leftJoin(participation.user)
            .where(
                participation.routine.id.eq(routineId),
                participation.user.kakaoId.eq(userId)
            ).fetchFirst() != null
    }

}