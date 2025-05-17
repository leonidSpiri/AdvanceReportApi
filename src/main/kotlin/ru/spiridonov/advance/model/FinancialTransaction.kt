package ru.spiridonov.advance.model

import jakarta.persistence.*
import ru.spiridonov.advance.model.enums.TransactionType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "financial_transactions")
data class FinancialTransaction(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(precision = 10, scale = 2)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    val type: TransactionType,

    val description: String? = null,

    val transactionDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    val createdBy: User,

    val createdAt: LocalDateTime = LocalDateTime.now()
)
