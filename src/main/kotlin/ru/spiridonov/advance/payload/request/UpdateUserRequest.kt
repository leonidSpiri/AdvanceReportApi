package ru.spiridonov.advance.payload.request

data class UpdateUserRequest(
    val fullName: String?,
    val email: String?,
    val departmentId: Long?
)