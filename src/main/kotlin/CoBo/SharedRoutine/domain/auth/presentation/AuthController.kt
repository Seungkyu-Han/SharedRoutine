package CoBo.SharedRoutine.domain.auth.presentation

import CoBo.SharedRoutine.domain.auth.Data.Dto.AuthGetLoginRes
import CoBo.SharedRoutine.domain.auth.application.AuthService
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.Parameters
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증")
class AuthController(private val authService: AuthService) {

    @GetMapping("/login")
    @Operation(summary = "로그인 API", description = "회원이 아니면 회원가입을 진행합니다.")
    @Parameters(
        Parameter(name = "code", description = "카카오 로그인 code")
    )
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content(schema = Schema(implementation = AuthGetLoginRes::class))))
    )
    fun login(@RequestParam code: String):ResponseEntity<CoBoResponseDto<AuthGetLoginRes>>{
        return authService.getLogin(code)
    }
}