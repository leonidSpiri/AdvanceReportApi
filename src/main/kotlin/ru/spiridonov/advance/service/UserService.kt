package ru.spiridonov.advance.service

import jakarta.persistence.EntityNotFoundException
import jakarta.transaction.Transactional
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import ru.spiridonov.advance.extensions.toDto
import ru.spiridonov.advance.model.User
import ru.spiridonov.advance.payload.UserDto
import ru.spiridonov.advance.payload.request.ChangePasswordRequest
import ru.spiridonov.advance.payload.request.CreateUserRequest
import ru.spiridonov.advance.payload.request.UpdateUserRequest
import ru.spiridonov.advance.repository.UserRepository
import java.time.LocalDateTime

@Service
@Transactional
class UserService(
    val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun createUser(request: CreateUserRequest): UserDto {
        if (userRepository.existsByUsername(request.username)) {
            throw IllegalArgumentException("Username already exists")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            username = request.username,
            passwordHash = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            email = request.email,
            role = request.role,
            departmentId = request.departmentId
        )

        return userRepository.save(user).toDto()
    }

    fun getUserById(id: Long): UserDto {
        return userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User not found") }
            .toDto()
    }

    fun getAllUsers(): List<UserDto> {
        return userRepository.findAll().map { it.toDto() }
    }

    fun updateUser(id: Long, request: UpdateUserRequest): UserDto {
        val user = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User not found") }

        request.fullName?.let { user.fullName = it }
        request.email?.let {
            if (userRepository.existsByEmail(it) && user.email != it) {
                throw IllegalArgumentException("Email already exists")
            }
            user.email = it
        }
        request.departmentId?.let { user.departmentId = it }
        user.updatedAt = LocalDateTime.now()

        return userRepository.save(user).toDto()
    }

    fun changePassword(id: Long, request: ChangePasswordRequest) {
        val user = userRepository.findById(id)
            .orElseThrow { EntityNotFoundException("User not found") }

        if (!passwordEncoder.matches(request.currentPassword, user.passwordHash)) {
            throw IllegalArgumentException("Current password is incorrect")
        }

        user.passwordHash = passwordEncoder.encode(request.newPassword)
        user.updatedAt = LocalDateTime.now()
        userRepository.save(user)
    }
}