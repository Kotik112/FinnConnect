package com.example.dao

import com.example.model.User
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.SQLException

class UserRepository(
    private val connection: Connection
) {
    private val logger = LoggerFactory.getLogger(UserRepository::class.java)

    companion object {
        private const val CREATE_TABLE_USERS =
            """
            CREATE TABLE IF NOT EXISTS users (
                id UUID PRIMARY KEY,                        -- UUID as the primary key
                username VARCHAR(50) NOT NULL UNIQUE,       -- Username must be unique
                email VARCHAR(255) NOT NULL UNIQUE,         -- Email must be unique and non-null
                full_name VARCHAR(255) NOT NULL,            -- Full name of the user
                password_hash VARCHAR(255) NOT NULL,        -- Hashed password
                role VARCHAR(10) NOT NULL,                  -- User role (ADMIN, USER, GUEST)
                remember_me BOOLEAN DEFAULT FALSE,          -- Remember me flag
                created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP, -- Creation timestamp
                last_updated_at TIMESTAMP WITH TIME ZONE,   -- Optional last update timestamp
                last_login_at TIMESTAMP WITH TIME ZONE      -- Optional last login timestamp
);
            """

        private const val SAVE_USER_SQL = """
            INSERT INTO users (id, username, email, full_name, password_hash, role, remember_me)
            VALUES (?,?,?,?,?,?,?)
            ON CONFLICT (username, email) DO UPDATE SET 
                full_name = EXCLUDED.full_name,
                password_hash = EXCLUDED.password_hash,
                role = EXCLUDED.role,
                remember_me = EXCLUDED.remember_me,
                last_updated_at = CURRENT_TIMESTAMP
            RETURNING id;
        """
    }

    init {
        connection.createStatement().use { it.executeUpdate(CREATE_TABLE_USERS) }
        logger.info("User table created or already exists.")
    }

    fun saveUser(user: User): Int {
        connection.prepareStatement(SAVE_USER_SQL).use { statement ->
            statement.setObject(1, user.id)
            statement.setString(2, user.username)
            statement.setString(3, user.email)
            statement.setString(4, user.fullName)
            statement.setString(5, user.passwordHash)
            statement.setString(6, user.role.name)
            statement.setBoolean(8, user.rememberMe)

            val result = statement.executeQuery()
            return if (result.next()) {
                result.getInt("id")
            }
            else {
                throw SQLException("Failed to save user, no ID returned.")
            }
        }
    }
}