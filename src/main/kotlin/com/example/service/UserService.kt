package com.example.service

import com.example.dao.UserRepository
import com.example.model.User
import com.example.model.UserDto
import org.mindrot.jbcrypt.BCrypt
import org.slf4j.LoggerFactory

class UserService(private val userRepository: UserRepository) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun getUserByUsername(username: String, password: String): UserDto? {
        val passwordHash = password.hash()
        val user = userRepository.getUserByUsernameAndPassword(username, passwordHash)
        return user
    }

    fun saveUser(user: User): UserDto {
        val hashedPassword = user.password.hash()
        val newUser = user.copy(password = hashedPassword)
        val result = userRepository.saveUser(newUser)
        return if(result.toString().isNotEmpty()) {
            logger.info("User saved successfully")
            UserDto(
                username = newUser.username,
                email = newUser.email,
                fullName = newUser.fullName,
                role = newUser.role
            )
        } else {
            logger.error("Failed to save user")
            throw IllegalArgumentException("Failed to save user")
        }
    }


    private fun String.hash(): String {
        val hash = BCrypt.hashpw(this, BCrypt.gensalt())
        logger.debug("$this hashed to: $hash")
        return hash
    }
}