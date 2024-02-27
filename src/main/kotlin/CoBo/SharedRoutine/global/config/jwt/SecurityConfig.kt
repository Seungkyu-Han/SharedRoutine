package CoBo.SharedRoutine.global.config.jwt

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity.RequestMatcherConfigurer
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component

@Component
class SecurityConfig(
    private val jwtFilter: JwtFilter, private val jwtExceptionFilter: JwtExceptionFilter) {

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain{
        return httpSecurity
            .securityMatchers { matchers: RequestMatcherConfigurer -> matchers.requestMatchers("/**") }
            .csrf{obj: CsrfConfigurer<HttpSecurity> -> obj.disable()}
            .sessionManagement{httpSecuritySessionManagementConfigurer: SessionManagementConfigurer<HttpSecurity?> ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .httpBasic { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }
            .authorizeHttpRequests { authorize ->
                authorize.requestMatchers("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**").permitAll()
            }
            .formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() }
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterBefore(jwtExceptionFilter, JwtFilter::class.java)
            .build()
    }

}