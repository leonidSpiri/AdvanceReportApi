package ru.spiridonov.advance.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.spiridonov.advance.extensions.getUserByUsername
import ru.spiridonov.advance.model.enums.UserRole
import ru.spiridonov.advance.payload.FinancialTransactionDto
import ru.spiridonov.advance.payload.request.CreateFinancialTransactionRequest
import ru.spiridonov.advance.service.AdvanceReportService
import ru.spiridonov.advance.service.FinancialService
import ru.spiridonov.advance.service.UserService
import java.math.BigDecimal

@RestController
@RequestMapping("/api/v1/finance")
@SecurityRequirement(name = "bearerAuth")
class FinancialController(
    private val financialService: FinancialService,
    private val advanceReportService: AdvanceReportService,
    private val userService: UserService
) {
    @GetMapping("/users/balance")
    fun getUserBalance(
        authentication: Authentication
    ): ResponseEntity<BigDecimal> {
        val currentUser = userService.getUserByUsername(authentication.name)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(advanceReportService.getUserBalance(currentUser.id))
    }

    @PostMapping("/transactions")
    @PreAuthorize("hasRole('ACCOUNTING') or hasRole('ADMIN')")
    fun createTransaction(
        @RequestBody request: CreateFinancialTransactionRequest,
        authentication: Authentication
    ): ResponseEntity<FinancialTransactionDto> {
        val currentUser = userService.getUserByUsername(authentication.name)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(financialService.createTransaction(request, currentUser.id))
    }

    @GetMapping("/transactions")
    fun getTransactions(
        @RequestParam(required = false) userId: Long?,
        authentication: Authentication
    ): ResponseEntity<List<FinancialTransactionDto>> {
        val currentUser = userService.getUserByUsername(authentication.name)

        return when {
            userId != null && (currentUser.role == UserRole.ACCOUNTING || currentUser.role == UserRole.ADMIN) ->
                ResponseEntity.ok(financialService.getTransactionsByUser(userId))

            currentUser.role == UserRole.EMPLOYEE ->
                ResponseEntity.ok(financialService.getTransactionsByUser(currentUser.id))

            currentUser.role == UserRole.ACCOUNTING || currentUser.role == UserRole.ADMIN ->
                ResponseEntity.ok(financialService.getAllTransactions())

            else -> ResponseEntity.ok(financialService.getTransactionsByUser(currentUser.id))
        }
    }
}