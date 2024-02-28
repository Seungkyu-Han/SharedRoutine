package CoBo.SharedRoutine.domain.auth.application

import CoBo.SharedRoutine.domain.auth.Data.Dto.AuthGetLoginRes
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import org.springframework.http.ResponseEntity

interface AuthService {
    fun getLogin(code: String): ResponseEntity<CoBoResponseDto<AuthGetLoginRes>>
}