package CoBo.SharedRoutine.domain.user.application.Impl

import CoBo.SharedRoutine.domain.user.application.UserService
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
import java.util.UUID

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
    private val amazonS3Client: AmazonS3Client,
    @Value("\${cloud.aws.s3.bucket}")
    private val bucket: String): UserService {

    private val profileImagePath = "profileImage/"
    override fun postImage(
        multipartFile: MultipartFile,
        authentication: Authentication
    ): ResponseEntity<CoBoResponseDto<CoBoResponseStatus>> {
        uploadImageToS3(multipartFile)

        return CoBoResponse<CoBoResponseStatus>(CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    private fun uploadImageToS3(multipartFile: MultipartFile){
        val uuidName = profileImagePath + UUID.randomUUID() + "." + getExtension(multipartFile.originalFilename)

        val metadata = ObjectMetadata()

        metadata.contentType = multipartFile.contentType
        metadata.contentLength = multipartFile.size

        amazonS3Client.putObject(bucket, uuidName, multipartFile.inputStream, metadata)

    }

    private fun getExtension(originalFileName: String?):String?{
        return StringUtils.getFilenameExtension(originalFileName)
    }
}