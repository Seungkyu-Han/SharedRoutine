package CoBo.SharedRoutine.global.repository.Custom

import CoBo.SharedRoutine.global.data.entity.User

interface UserRepositoryCustom {

    fun findByRefreshToken(refreshToken: String): User?
}