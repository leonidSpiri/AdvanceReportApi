package ru.spiridonov.advance.payload

import java.math.BigDecimal
import java.time.LocalDateTime

data class ReceiptDto(
    val id: Long?,
    val rawQrData: String?,
    val validated: Boolean,
    val storeName: String?,
    val storeInn: String?,
    val receiptNumber: String?,
    val receiptDate: LocalDateTime?,
    val totalAmount: BigDecimal?,
    val validatedAt: LocalDateTime?,
    val items: List<ReceiptItemDto>,
    val attachments: List<AttachmentDto>,
    val createdAt: LocalDateTime
)