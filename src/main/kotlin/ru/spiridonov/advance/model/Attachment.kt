package ru.spiridonov.advance.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "attachments")
data class Attachment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Many-to-one relationship with Receipt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    val receipt: Receipt? = null,

    // For cases when an attachment is directly linked to an expense item
    // without going through a receipt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_item_id")
    val expenseItem: ExpenseItem? = null,

    val fileName: String,

    val fileType: String,

    val fileSize: Long,

    val storagePath: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)