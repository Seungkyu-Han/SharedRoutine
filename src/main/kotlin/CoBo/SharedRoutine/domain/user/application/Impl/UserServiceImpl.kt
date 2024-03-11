package CoBo.SharedRoutine.domain.user.application.Impl

import CoBo.SharedRoutine.domain.user.application.UserService
import CoBo.SharedRoutine.domain.user.data.dto.Res.UserGetRes
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import CoBo.SharedRoutine.global.repository.UserRepository
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ObjectMetadata
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import org.springframework.web.multipart.MultipartFile
import java.util.*
import java.util.concurrent.CompletableFuture
import kotlin.NoSuchElementException

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String,
    @Value("\${cloud.aws.s3.profileImagePath}")
    private val profileImagePath: String,
    @Value("\${cloud.aws.s3.s3BucketPrefix}")
    private val s3BucketPrefix: String): UserService {

    override fun postImage(
        multipartFile: MultipartFile,
        authentication: Authentication
    ): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val userId = authentication.name.toInt()

        val userCompletableFuture = CompletableFuture.supplyAsync {
            userRepository.findById(userId).orElseThrow{throw NoSuchElementException("일치하는 사용자가 없습니다.")}
        }

        val fileCompletableFuture = CompletableFuture.supplyAsync {
            s3BucketPrefix + uploadImageToS3(multipartFile)
        }

        return CompletableFuture.allOf(userCompletableFuture, fileCompletableFuture)
            .thenApplyAsync {

                val user = userCompletableFuture.get()
                val file = fileCompletableFuture.get()

                user.image = file

                userRepository.save(user)

                CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
            }.get()
    }

    private fun uploadImageToS3(multipartFile: MultipartFile): String{
        val uuidName = profileImagePath + UUID.randomUUID() + "." + getExtension(multipartFile.originalFilename)

        val metadata = ObjectMetadata()

        metadata.contentType = multipartFile.contentType
        metadata.contentLength = multipartFile.size

        amazonS3Client.putObject(bucket, uuidName, multipartFile.inputStream, metadata)
        return uuidName
    }

    private fun getExtension(originalFileName: String?):String?{
        return StringUtils.getFilenameExtension(originalFileName)
    }

    override fun patch(newName: String, authentication: Authentication): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow {throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        user.name = newName
        userRepository.save(user)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun getExist(newName: String): ResponseEntity<CoBoResponseDto<Boolean>> {
        return CoBoResponse(userRepository.existsByName(newName), CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    override fun get(authentication: Authentication): ResponseEntity<CoBoResponseDto<UserGetRes>> {
        val user = userRepository.findById(authentication.name.toInt())
            .orElseThrow {throw NoSuchElementException("일치하는 사용자가 없습니다.")}

        return CoBoResponse(UserGetRes(user.name, user.image), CoBoResponseStatus.SUCCESS).getResponseEntity()
    }
}