package ru.spiridonov.advance.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.spiridonov.advance.extensions.getUserByUsername
import ru.spiridonov.advance.model.enums.UserRole
import ru.spiridonov.advance.payload.FinancialTransactionDto
import ru.spiridonov.advance.payload.UserBalanceDto
import ru.spiridonov.advance.payload.request.CreateFinancialTransactionRequest
import ru.spiridonov.advance.service.FinancialService
import ru.spiridonov.advance.service.UserService

@RestController
@RequestMapping("/api/v1/finance")
class FinancialController(
    private val financialService: FinancialService,
    private val userService: UserService
) {
    @GetMapping("/users/{userId}/balance")
    fun getUserBalance(@PathVariable userId: Long): ResponseEntity<UserBalanceDto> {
        return ResponseEntity.ok(financialService.getUserBalance(userId))
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