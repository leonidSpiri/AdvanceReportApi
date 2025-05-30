package ru.spiridonov.advance.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import ru.spiridonov.advance.extensions.getUserByUsername
import ru.spiridonov.advance.payload.UserDto
import ru.spiridonov.advance.payload.request.ChangePasswordRequest
import ru.spiridonov.advance.payload.request.CreateUserRequest
import ru.spiridonov.advance.payload.request.UpdateUserRequest
import ru.spiridonov.advance.service.UserService

@RestController
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "bearerAuth")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        return ResponseEntity.ok(userService.getAllUsers())
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.getUserById(id))
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun createUser(@RequestBody request: CreateUserRequest): ResponseEntity<UserDto> {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUser(@PathVariable id: Long, @RequestBody request: UpdateUserRequest): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.updateUser(id, request))
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("hasRole('ADMIN')")
    fun changePassword(@PathVariable id: Long, @RequestBody request: ChangePasswordRequest): ResponseEntity<Void> {
        userService.changePassword(id, request)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun getCurrentUser(authentication: Authentication): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.getUserByUsername(authentication.name))
    }
}