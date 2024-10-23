package com.example.model

import com.example.utils.TimeProvider
import java.time.LocalDateTime
import java.util.UUID

private val timeProvider = TimeProvider()

data class User(
    val id: UUID,
    val username: String,
    val email: String,
    val fullName: String,
    val passwordHash: String,
    val role: Role,
    val rememberMe: Boolean = false,
    val createdAt: LocalDateTime = timeProvider.getCurrentTime(),
    val lastUpdatedAt: LocalDateTime? = null,
    val lastLoginAt: LocalDateTime? = null,
)

enum class Role {
    ADMIN,
    USER,
    GUEST
}