package ru.spiridonov.advance.payload

import ru.spiridonov.advance.model.enums.UserRole

data class UserDto(
    val id: Long,
    val username: String,
    val fullName: String,
    val email: String,
    val role: UserRole,
    val departmentId: Long?
)