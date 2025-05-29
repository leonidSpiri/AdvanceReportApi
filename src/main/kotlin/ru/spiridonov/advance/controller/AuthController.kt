package ru.spiridonov.advance.controller

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import ru.spiridonov.advance.model.User
import ru.spiridonov.advance.model.enums.UserRole
import ru.spiridonov.advance.payload.LoginRequest
import ru.spiridonov.advance.payload.RefreshTokenRequest
import ru.spiridonov.advance.payload.SignupRequest
import ru.spiridonov.advance.payload.TokenResponse
import ru.spiridonov.advance.repository.UserRepository
import ru.spiridonov.advance.security.JwtTokenProvider
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Value("\${jwt.access-token-validity}")
    private val accessTokenValidityMs: Long = 0

    @PostMapping("/login")
    fun login(@Valid @RequestBody loginRequest: LoginRequest): ResponseEntity<TokenResponse> {
        val authentication: Authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )

        val accessToken = jwtTokenProvider.createAccessToken(
            loginRequest.username,
            authentication.authorities.map { it.authority }
        )
        val refreshToken = jwtTokenProvider.createRefreshToken(loginRequest.username)

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = TimeUnit.MILLISECONDS.toSeconds(accessTokenValidityMs)
            )
        )
    }

    @PostMapping("/signup")
    fun signup(@Valid @RequestBody signupRequest: SignupRequest): ResponseEntity<Any> {
        if (userRepository.existsByUsername(signupRequest.username)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is already taken")
        }

        if (userRepository.existsByEmail(signupRequest.email)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use")
        }

        val userRole = try {
            UserRole.valueOf(signupRequest.role.uppercase())
        } catch (e: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role specified, ${e.toString()}")
        }

        val user = User(
            username = signupRequest.username,
            passwordHash = passwordEncoder.encode(signupRequest.password),
            fullName = signupRequest.fullName,
            email = signupRequest.email,
            role = userRole
        )

        userRepository.save(user)

        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("message" to "User registered successfully"))
    }

    @PostMapping("/refresh")
    fun refreshToken(@Valid @RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<TokenResponse> {
        if (!jwtTokenProvider.validateToken(refreshTokenRequest.refreshToken)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token")
        }

        val username = jwtTokenProvider.getUsername(refreshTokenRequest.refreshToken)
        val user = userRepository.findByUsername(username)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "User not found") }

        val authorities = listOf("ROLE_${user.role.name}")
        val accessToken = jwtTokenProvider.createAccessToken(username, authorities)
        val refreshToken = jwtTokenProvider.createRefreshToken(username)

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                expiresIn = 900 // 15 minutes in seconds
            )
        )
    }
}