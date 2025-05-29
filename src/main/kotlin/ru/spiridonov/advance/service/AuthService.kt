package ru.spiridonov.advance.service

import jakarta.transaction.Transactional
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.payload.request.LoginRequest
import ru.spiridonov.advance.payload.request.RefreshTokenRequest
import ru.spiridonov.advance.payload.response.LoginResponse
import ru.spiridonov.advance.repository.UserRepository
import ru.spiridonov.advance.security.JwtTokenProvider

@Service
@Transactional
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    fun login(request: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(request.username)
            .orElseThrow { BadCredentialsException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw BadCredentialsException("Invalid credentials")
        }

        val accessToken = jwtTokenProvider.createAccessToken(user.username, listOf("ROLE_${user.role.name}"))
        val refreshToken = jwtTokenProvider.createRefreshToken(user.username)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user.toDto()
        )
    }

    fun refreshToken(request: RefreshTokenRequest): LoginResponse {
        if (!jwtTokenProvider.validateToken(request.refreshToken)) {
            throw BadCredentialsException("Invalid refresh token")
        }

        val username = jwtTokenProvider.getUsername(request.refreshToken)
        val user = userRepository.findByUsername(username)
            .orElseThrow { BadCredentialsException("User not found") }

        val accessToken = jwtTokenProvider.createAccessToken(user.username, listOf("ROLE_${user.role.name}"))
        val refreshToken = jwtTokenProvider.createRefreshToken(user.username)

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = user.toDto()
        )
    }
}