package ru.spiridonov.advance.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.spiridonov.advance.extensions.getUserByUsername
import ru.spiridonov.advance.model.enums.ReportStatus
import ru.spiridonov.advance.model.enums.UserRole
import ru.spiridonov.advance.payload.AdvanceReportDto
import ru.spiridonov.advance.payload.request.*
import ru.spiridonov.advance.service.AdvanceReportService
import ru.spiridonov.advance.service.UserService

@RestController
@RequestMapping("/api/v1/reports")
class AdvanceReportController(
    private val advanceReportService: AdvanceReportService,
    private val userService: UserService
) {
    @GetMapping
    fun getReports(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) supervisorId: Long?,
        @RequestParam(required = false) status: ReportStatus?,
        authentication: Authentication
    ): ResponseEntity<List<AdvanceReportDto>> {
        val currentUser = userService.getUserByUsername(authentication.name)

        return when {
            userId != null -> ResponseEntity.ok(advanceReportService.getReportsByUser(userId))
            supervisorId != null -> ResponseEntity.ok(advanceReportService.getReportsBySupervisor(supervisorId))
            status != null -> ResponseEntity.ok(advanceReportService.getReportsByStatus(status))
            currentUser.role == UserRole.EMPLOYEE -> ResponseEntity.ok(advanceReportService.getReportsByUser(currentUser.id))
            currentUser.role == UserRole.SUPERVISOR -> ResponseEntity.ok(
                advanceReportService.getReportsBySupervisor(
                    currentUser.id
                )
            )

            else -> ResponseEntity.ok(advanceReportService.getAllReports())
        }
    }

    @GetMapping("/{id}")
    fun getReportById(@PathVariable id: Long): ResponseEntity<AdvanceReportDto> {
        return ResponseEntity.ok(advanceReportService.getReportById(id))
    }

    @PostMapping
    fun createReport(
        @RequestBody request: CreateAdvanceReportRequest,
        authentication: Authentication
    ): ResponseEntity<AdvanceReportDto> {
        val currentUser = userService.getUserByUsername(authentication.name)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(advanceReportService.createReport(currentUser.id, request))
    }

    @PutMapping("/{id}")
    fun updateReport(
        @PathVariable id: Long,
        @RequestBody request: UpdateAdvanceReportRequest
    ): ResponseEntity<AdvanceReportDto> {
        return ResponseEntity.ok(advanceReportService.updateReport(id, request))
    }

    @DeleteMapping("/{id}")
    fun deleteReport(@PathVariable id: Long): ResponseEntity<Void> {
        advanceReportService.deleteReport(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/submit")
    fun submitReport(@PathVariable id: Long, @RequestParam supervisorId: Long): ResponseEntity<AdvanceReportDto> {
        return ResponseEntity.ok(advanceReportService.submitReport(id, supervisorId))
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
    fun approveReport(
        @PathVariable id: Long,
        @RequestBody request: ApproveReportRequest
    ): ResponseEntity<AdvanceReportDto> {
        return ResponseEntity.ok(advanceReportService.approveReport(id, request))
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
    fun rejectReport(
        @PathVariable id: Long,
        @RequestBody request: RejectReportRequest
    ): ResponseEntity<AdvanceReportDto> {
        return ResponseEntity.ok(advanceReportService.rejectReport(id, request))
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ACCOUNTING') or hasRole('ADMIN')")
    fun archiveReport(
        @PathVariable id: Long,
        @RequestBody request: ArchiveReportRequest
    ): ResponseEntity<AdvanceReportDto> {
        return ResponseEntity.ok(advanceReportService.archiveReport(id, request))
    }
}