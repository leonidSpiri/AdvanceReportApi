package ru.spiridonov.advance.payload

import java.time.LocalDateTime

data class AttachmentDto(
    val id: Long?,
    val fileName: String,
    val fileType: String,
    val fileSize: Long,
    val createdAt: LocalDateTime
)