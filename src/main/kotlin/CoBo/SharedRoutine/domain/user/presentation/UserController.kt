package CoBo.SharedRoutine.domain.user.presentation

import CoBo.SharedRoutine.domain.user.application.UserService
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/user")
@Tag(name = "유저")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "유저 프로필 이미지 변경")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "성공", content = arrayOf(Content()))
    )
    fun postImage(@RequestPart multipartFile: MultipartFile, @Parameter(hidden = true) authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>>{
        return userService.postImage(multipartFile, authentication)
    }


}