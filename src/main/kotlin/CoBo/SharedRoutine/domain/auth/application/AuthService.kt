package CoBo.SharedRoutine.domain.auth.application

import CoBo.SharedRoutine.domain.auth.Data.Dto.AuthLoginRes
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import org.springframework.http.ResponseEntity

interface AuthService {
    fun getLogin(code: String): ResponseEntity<CoBoResponseDto<AuthLoginRes>>
    fun patchLogin(refreshToken: String): ResponseEntity<CoBoResponseDto<AuthLoginRes>>
}