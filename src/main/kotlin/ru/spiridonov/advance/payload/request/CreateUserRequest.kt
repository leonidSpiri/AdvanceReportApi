package ru.spiridonov.advance.payload.request

import ru.spiridonov.advance.model.enums.UserRole

data class CreateUserRequest(
    val username: String,
    val password: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val departmentId: Long?
)