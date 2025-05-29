package ru.spiridonov.advance.payload.request

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)