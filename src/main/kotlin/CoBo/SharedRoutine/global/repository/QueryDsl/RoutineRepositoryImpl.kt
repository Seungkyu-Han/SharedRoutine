package CoBo.SharedRoutine.global.repository.QueryDsl

import CoBo.SharedRoutine.global.data.entity.QRoutine.routine
import CoBo.SharedRoutine.global.data.entity.Routine
import CoBo.SharedRoutine.global.repository.Custom.RoutineRepositoryCustom
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class RoutineRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory): RoutineRepositoryCustom {

    override fun findTopTen(): List<Routine> {
        return jpaQueryFactory.select(routine)
            .from(routine)
            .orderBy(routine.memberCount.desc())
            .limit(10)
            .fetch()
    }
}