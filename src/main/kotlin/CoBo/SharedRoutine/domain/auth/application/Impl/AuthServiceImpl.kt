package CoBo.SharedRoutine.domain.auth.application.Impl

import CoBo.SharedRoutine.domain.auth.Data.Dto.AuthLoginRes
import CoBo.SharedRoutine.domain.auth.application.AuthService
import CoBo.SharedRoutine.global.config.jwt.JwtTokenProvider
import CoBo.SharedRoutine.global.config.response.CoBoResponse
import CoBo.SharedRoutine.global.config.response.CoBoResponseDto
import CoBo.SharedRoutine.global.config.response.CoBoResponseStatus
import CoBo.SharedRoutine.global.data.entity.User
import CoBo.SharedRoutine.global.repository.UserRepository
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@Service
class AuthServiceImpl(
    @Value("\${kakao.auth.client_id}")
    private val clientId: String,
    @Value("\${kakao.auth.redirect_uri}")
    private val redirectUri: String,
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
): AuthService {
    override fun patchLogin(refreshToken: String): ResponseEntity<CoBoResponseDto<AuthLoginRes>> {

        val id = jwtTokenProvider.getUserId(refreshToken.split(" ")[1]) ?: 0

        val accessToken = jwtTokenProvider.createAccessToken(id)

        return CoBoResponse(
            AuthLoginRes(accessToken = accessToken, refreshToken = refreshToken), CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    private val reqUrl = "https://kauth.kakao.com/oauth/token"
    override fun getLogin(code: String): ResponseEntity<CoBoResponseDto<AuthLoginRes>> {
        val kakaoAccessToken = getKakaoAccessToken(code)

        val element = getJsonElementByAccessToken(kakaoAccessToken)

        val id = element.asJsonObject["id"].asInt

        val user = userRepository.findById(id).orElse(register(id))
        val accessToken = jwtTokenProvider.createAccessToken(id)
        val refreshToken = jwtTokenProvider.createRefreshToken(id)

        user.refreshToken = refreshToken

        userRepository.save(user)

        return CoBoResponse(AuthLoginRes(
            accessToken = accessToken, refreshToken = refreshToken), CoBoResponseStatus.SUCCESS).getResponseEntity()
    }

    private fun register(kakaoId: Int): User {
        val user = User(kakaoId = kakaoId, name = null, refreshToken = null, followerCount = 0, followingCount = 0, image = null)
        userRepository.save(user)
        return user
    }

    private fun getKakaoAccessToken(code: String): String {
        val accessToken:String

        val url = URL(reqUrl)
        val httpURLConnection = url.openConnection() as HttpURLConnection

        httpURLConnection.requestMethod = "POST"
        httpURLConnection.doOutput = true

        val bufferedWriter = BufferedWriter(OutputStreamWriter(httpURLConnection.outputStream))
        val stringBuilder = "grant_type=authorization_code" +
                "&client_id=" + clientId +
                "&redirect_uri=" + redirectUri +
                "&code=" + code
        bufferedWriter.write(stringBuilder)
        bufferedWriter.flush()

        httpURLConnection.responseCode

        val element: JsonElement = getJsonElement(httpURLConnection)

        accessToken = element.asJsonObject.get("access_token").asString

        bufferedWriter.close()

        return accessToken

    }

    private fun getJsonElementByAccessToken(token : String): JsonElement {
        val reqUrl = "https://kapi.kakao.com/v2/user/me"

        val url = URL(reqUrl)
        val httpURLConnection = url.openConnection() as HttpURLConnection

        httpURLConnection.requestMethod = "POST"
        httpURLConnection.doOutput = true
        httpURLConnection.setRequestProperty("Authorization", "Bearer $token")

        return getJsonElement(httpURLConnection)
    }

    private fun getJsonElement(httpURLConnection: HttpURLConnection): JsonElement {
        val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
        val result = StringBuilder()

        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            result.append(line)
        }

        bufferedReader.close()

        val jsonString = result.toString()
        return JsonParser.parseString(jsonString)
    }
}