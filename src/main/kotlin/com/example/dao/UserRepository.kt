package com.example.dao

import com.example.model.Role
import com.example.model.User
import com.example.model.UserDto
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException
import java.util.*

class UserRepository(
    private val connection: Connection
) {
    private val logger = LoggerFactory.getLogger(UserRepository::class.java)

    companion object {
        private const val CREATE_TABLE_USERS =
            """
    CREATE TABLE IF NOT EXISTS users (
        id UUID DEFAULT gen_random_uuid() PRIMARY KEY,                          -- UUID as the primary key
        username VARCHAR(50) NOT NULL UNIQUE,                                   -- Username must be unique
        email VARCHAR(255) NOT NULL UNIQUE,                                     -- Email must be unique and non-null
        full_name VARCHAR(255) NOT NULL,                                        -- Full name of the user
        password_hash VARCHAR(255) NOT NULL,                                    -- Hashed password
        role VARCHAR(10) NOT NULL,                                              -- User role (ADMIN, USER, GUEST)
        remember_me BOOLEAN DEFAULT FALSE,                                      -- Remember me flag
        created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Creation timestamp
        last_updated_at TIMESTAMP WITH TIME ZONE,                               -- Optional last update timestamp
        last_login_at TIMESTAMP WITH TIME ZONE,                                 -- Optional last login timestamp
        CONSTRAINT unique_username_email UNIQUE (username, email)               -- Unique constraint on username and email combo
    );
    """

        private const val SAVE_USER_SQL = """
            INSERT INTO users (username, email, full_name, password_hash, role, remember_me)
            VALUES (?,?,?,?,?,?)
            ON CONFLICT (username, email) DO UPDATE SET 
                full_name = EXCLUDED.full_name,
                password_hash = EXCLUDED.password_hash,
                role = EXCLUDED.role,
                remember_me = EXCLUDED.remember_me,
                last_updated_at = CURRENT_TIMESTAMP
            RETURNING id;
        """

        private val GET_USER_BY_USERNAME_AND_PASSWORD = """
            SELECT username, email, full_name, role
            FROM users
            WHERE username = ? and password_hash = ?;
        """.trimIndent()
    }

    init {
        connection.createStatement().use { it.executeUpdate(CREATE_TABLE_USERS) }
        logger.info("User table created or already exists.")
    }

    fun saveUser(user: User): UUID {
        connection.prepareStatement(SAVE_USER_SQL).use { statement ->
            statement.setString(1, user.username)
            statement.setString(2, user.email)
            statement.setString(3, user.fullName)
            statement.setString(4, user.password)
            statement.setString(5, user.role.name)
            statement.setBoolean(6, user.rememberMe)

            val result = statement.executeQuery()
            return if (result.next()) {
                result.getObject("id", UUID::class.java)
            }
            else {
                throw SQLException("Failed to save user, no ID returned.")
            }
        }
    }

    fun getUserByUsernameAndPassword(username: String, passwordHash: String): UserDto? {
        connection.prepareStatement(GET_USER_BY_USERNAME_AND_PASSWORD).use { statement ->
            statement.setString(1, username)
            statement.setString(2, passwordHash)
            logger.debug("Executing query: $GET_USER_BY_USERNAME_AND_PASSWORD with parameters: $username, $passwordHash")

            val resultSet = statement.executeQuery()
            return if (resultSet.next()) {
                UserDto(
                    username = resultSet.getString("username"),
                    email = resultSet.getString("email"),
                    fullName = resultSet.getString("full_name"),
                    role = Role.valueOf(resultSet.getString("role"))
                )
            } else {
                logger.debug("Executing query: $GET_USER_BY_USERNAME_AND_PASSWORD with parameters: $username, $passwordHash")
                null
                //throw SQLException("User not found with username: $username")
            }
        }

    }
}