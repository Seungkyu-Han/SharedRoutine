package CoBo.SharedRoutine.global.repository.QueryDsl

import CoBo.SharedRoutine.global.data.entity.QUser.user
import CoBo.SharedRoutine.global.data.entity.User
import CoBo.SharedRoutine.global.repository.Custom.UserRepositoryCustom
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(private val jpaQueryFactory: JPAQueryFactory): UserRepositoryCustom{

    override fun findByRefreshToken(refreshToken: String): User? {
        return jpaQueryFactory.select(user)
                .from(user)
                .where(user.refreshToken.eq(refreshToken))
                .fetchOne()
    }
}