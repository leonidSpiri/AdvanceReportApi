package ru.spiridonov.advance.model

import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "receipt_items")
data class ReceiptItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    val receipt: Receipt,

    val name: String,

    val quantity: Double,

    @Column(precision = 10, scale = 2)
    val price: BigDecimal,

    @Column(precision = 10, scale = 2)
    val amount: BigDecimal,

    val taxRate: Int? = null
)
