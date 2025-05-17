package ru.spiridonov.advance.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.spiridonov.advance.model.User
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
}