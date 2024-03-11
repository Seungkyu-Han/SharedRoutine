package CoBo.SharedRoutine.global.repository

import CoBo.SharedRoutine.global.data.entity.User
import CoBo.SharedRoutine.global.repository.Custom.UserRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Int>, UserRepositoryCustom {
    fun existsByName(name: String): Boolean
}