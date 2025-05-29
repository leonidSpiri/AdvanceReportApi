package ru.spiridonov.advance.payload

import jakarta.validation.constraints.NotBlank

data class SignupRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String,

    @field:NotBlank(message = "Full name is required")
    val fullName: String,

    @field:NotBlank(message = "Email is required")
    val email: String,

    val role: String
)