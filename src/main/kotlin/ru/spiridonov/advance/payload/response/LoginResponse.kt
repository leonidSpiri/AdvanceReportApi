package ru.spiridonov.advance.payload.response

import ru.spiridonov.advance.payload.UserDto

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserDto
)