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

    var description: String,

    var date: LocalDate,

    var quantity: Int,

    @Column(precision = 10, scale = 2)
    var unitPrice: BigDecimal,

    @Column(precision = 10, scale = 2)
    var totalCost: BigDecimal,

    var hasReceipt: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    var receipt: Receipt? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)