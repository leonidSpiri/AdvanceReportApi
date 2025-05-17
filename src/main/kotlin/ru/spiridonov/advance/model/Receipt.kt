package ru.spiridonov.advance.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "receipts")
data class Receipt(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val rawQrData: String? = null,

    val validated: Boolean = false,

    val storeName: String? = null,

    val storeInn: String? = null,

    val receiptNumber: String? = null,

    val receiptDate: LocalDateTime? = null,

    @Column(precision = 10, scale = 2)
    val totalAmount: BigDecimal? = null,

    var validatedAt: LocalDateTime? = null,

    // One-to-many relationship with ReceiptItems
    @OneToMany(mappedBy = "receipt", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableList<ReceiptItem> = mutableListOf(),

    // One-to-many relationship with Attachments
    @OneToMany(mappedBy = "receipt", cascade = [CascadeType.ALL], orphanRemoval = true)
    val attachments: MutableList<Attachment> = mutableListOf(),

    val createdAt: LocalDateTime = LocalDateTime.now()
)