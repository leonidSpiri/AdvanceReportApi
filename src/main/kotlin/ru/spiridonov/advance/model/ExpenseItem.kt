package ru.spiridonov.advance.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "expense_items")
data class ExpenseItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id")
    val report: AdvanceReport,

    val description: String,

    val date: LocalDate,

    val quantity: Int,

    @Column(precision = 10, scale = 2)
    val unitPrice: BigDecimal,

    @Column(precision = 10, scale = 2)
    val totalCost: BigDecimal,

    val hasReceipt: Boolean = false,

    var receiptId: Long? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)