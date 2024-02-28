package CoBo.SharedRoutine.global.config.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.util.*

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret-key}")
    val secretKey: String
) {

    private final val accessTokenValidTime = Duration.ofHours(2).toMillis()
    private final val refreshTokenValidTime = Duration.ofDays(7).toMillis()

    fun getUserId(token: String): Integer?{
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .body.get("userId", java.lang.Integer::class.java)
    }

    fun isAccessToken(token: String): Boolean{
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .header["type"].toString() == "access"
    }

    fun createAccessToken(userId: Int): String{
        return createJwtToken(userId, secretKey, "access", accessTokenValidTime)
    }

    fun createRefreshToken(userId: Int): String{
        return createJwtToken(userId, secretKey, "refresh", refreshTokenValidTime)
    }

    fun createJwtToken(userId: Int, secretKey: String, type: String, tokenValidTime: Long): String{
        val claims = Jwts.claims()
        claims["userId"] = userId

        return Jwts.builder()
            .setHeaderParam("type", type)
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + tokenValidTime))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact()
    }

}