package CoBo.SharedRoutine.domain.auth.Data.Dto

import io.swagger.v3.oas.annotations.media.Schema

data class AuthGetLoginRes(
    @Schema(description = "재발급 토큰")
    val refreshToken: String,

    @Schema(description = "사용 토큰")
    val accessToken: String
)
