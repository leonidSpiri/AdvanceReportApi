package ru.spiridonov.advance.model

import jakarta.persistence.*
import ru.spiridonov.advance.model.enums.ReportStatus
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "advance_reports")
data class AdvanceReport(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var title: String,

    var project: String,

    var businessTripLocation: String,

    var startDate: LocalDate,

    var endDate: LocalDate,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    var supervisor: User? = null,

    @Enumerated(EnumType.STRING)
    var status: ReportStatus = ReportStatus.DRAFT,

    var rejectionReason: String? = null,

    @Column(precision = 10, scale = 2)
    var previousBalance: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 10, scale = 2)
    var totalAmount: BigDecimal = BigDecimal.ZERO,

    @Column(precision = 10, scale = 2)
    var currentBalance: BigDecimal = BigDecimal.ZERO,

    // Additional information field
    @Column(columnDefinition = "TEXT")
    var additionalInfo: String? = null,

    // Comment fields for an approval/rejection process
    @Column(columnDefinition = "TEXT")
    var supervisorComments: String? = null,

    @Column(columnDefinition = "TEXT")
    var accountingComments: String? = null,

    // Accounting reference number (for integration with accounting systems)
    var accountingReferenceNumber: String? = null,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now()
)
