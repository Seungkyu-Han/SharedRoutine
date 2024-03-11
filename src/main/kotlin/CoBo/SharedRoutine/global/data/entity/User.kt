package CoBo.SharedRoutine.global.data.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class User(
    @Id
    var kakaoId: Int,
    @Column(unique = true)
    var name: String,
    var refreshToken: String?,
    var followerCount: Int,
    var followingCount: Int,
    var image: String?
)
