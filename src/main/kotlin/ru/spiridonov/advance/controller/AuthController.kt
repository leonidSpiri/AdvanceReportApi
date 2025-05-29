package ru.spiridonov.advance.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.spiridonov.advance.payload.request.LoginRequest
import ru.spiridonov.advance.payload.request.RefreshTokenRequest
import ru.spiridonov.advance.payload.response.LoginResponse
import ru.spiridonov.advance.service.AuthService

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(authService.login(request))
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<LoginResponse> {
        return ResponseEntity.ok(authService.refreshToken(request))
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Void> {
        // Implementation for token invalidation would go here
        return ResponseEntity.ok().build()
    }
}