package ru.spiridonov.advance.service

import org.springframework.stereotype.Service
import ru.spiridonov.advance.model.AdvanceReport
import ru.spiridonov.advance.model.enums.ReportStatus
import ru.spiridonov.advance.payload.AdvanceReportResponse
import ru.spiridonov.advance.repository.AdvanceReportRepository
import ru.spiridonov.advance.repository.UserRepository
import java.time.LocalDate

@Service
class AdvanceReportService(
    private val advanceReportRepository: AdvanceReportRepository,
    private val userRepository: UserRepository
) {

    fun getAllReportsForUser(userId: Long): List<AdvanceReportResponse> {
        val user = userRepository.findById(userId)
            .orElseThrow { NoSuchElementException("User with id $userId not found") }
        return advanceReportRepository.findByUser(user).map { it.toDto() }
    }

    fun getReportById(id: Long): AdvanceReportResponse {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { NoSuchElementException("Report with id $id not found") }
        return report.toDto()
    }

    fun createReport(dto: AdvanceReportResponse): AdvanceReportResponse {
        val user = userRepository.findById(dto.userId)
            .orElseThrow { NoSuchElementException("User with id ${dto.userId} not found") }

        val supervisor = dto.supervisorId?.let { userRepository.findById(it).orElse(null) }

        val report = AdvanceReport(
            user = user,
            supervisor = supervisor,
            status = dto.status,
            additionalInfo = dto.description,
            title = dto.title,
            // TODO - отдать gpt задание разбить dto на request и response. в request просить только реально необходимые поля
        )

        return advanceReportRepository.save(report).toDto()
    }

    fun updateReport(id: Long, dto: AdvanceReportResponse): AdvanceReportResponse {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { NoSuchElementException("Report with id $id not found") }

        val supervisor = dto.supervisorId?.let { userRepository.findById(it).orElse(null) }

        report.apply {
            status = dto.status
            approvedDate = dto.approvedDate
            totalAmount = dto.totalAmount
            description = dto.description
            this.supervisor = supervisor
        }

        return advanceReportRepository.save(report).toDto()
    }

    fun deleteReport(id: Long) {
        if (!advanceReportRepository.existsById(id)) {
            throw NoSuchElementException("Report with id $id not found")
        }
        advanceReportRepository.deleteById(id)
    }

    fun approveReport(id: Long) {
        val report = advanceReportRepository.findById(id)
            .orElseThrow { NoSuchElementException("Report with id $id not found") }
        report.status = ReportStatus.APPROVED
        report.approvedDate = LocalDate.now()
        advanceReportRepository.save(report)
    }

    private fun AdvanceReport.toDto() = AdvanceReportResponse(
        id = this.id,
        userId = this.user.id!!,
        supervisorId = this.supervisor?.id,
        status = this.status,
        createdDate = this.createdDate,
        approvedDate = this.approvedDate,
        totalAmount = this.totalAmount,
        description = this.description
    )
}