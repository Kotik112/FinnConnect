package com.example.dao

import com.example.model.TokenResponse
import org.slf4j.LoggerFactory
import java.sql.Connection

/**
 * Repository for managing OAuth tokens using SQL with PreparedStatement.
 * Provides methods to save, retrieve, and delete tokens in the database.
 *
 * @property connection the SQL connection to the database.
 */
class OAuthTokenRepository(
    private val connection: Connection
) {
    private val logger = LoggerFactory.getLogger(OAuthTokenRepository::class.java)

    companion object {
        private const val CREATE_TABLE_OAUTH_TOKENS =
            """
            CREATE TABLE IF NOT EXISTS oauth_tokens (
                user_id VARCHAR(50) PRIMARY KEY,
                access_token VARCHAR(255) NOT NULL,
                refresh_token VARCHAR(255),
                expires_in INTEGER NOT NULL,
                token_type VARCHAR(50) NOT NULL,
                client_id VARCHAR(50) NOT NULL,
                issued_at BIGINT NOT NULL
            );
            """
        private const val INSERT_OR_UPDATE_OAUTH_TOKEN =
            """
            INSERT INTO oauth_tokens (user_id, access_token, refresh_token, expires_in, token_type, client_id, issued_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (user_id) DO UPDATE 
            SET access_token = EXCLUDED.access_token, 
                refresh_token = EXCLUDED.refresh_token, 
                expires_in = EXCLUDED.expires_in, 
                token_type = EXCLUDED.token_type, 
                client_id = EXCLUDED.client_id,
                issued_at = EXCLUDED.issued_at;
            """
        private const val SELECT_OAUTH_TOKEN_BY_USER_ID =
            "SELECT user_id, access_token, refresh_token, expires_in, token_type, client_id, issued_at FROM oauth_tokens WHERE user_id = ?"
    }

    /**
     * Initializes the repository by creating the oauth_tokens table if it doesn't exist.
     */
    init {
        connection.createStatement().use { it.executeUpdate(CREATE_TABLE_OAUTH_TOKENS) }
        logger.info("OAuth Token table created or already exists.")
    }

    /**
     * Saves a new token or updates an existing token in the database for the specified user ID.
     *
     * @param userId the unique identifier of the user.
     * @param tokenResponse the token data to be saved, including access token, refresh token, and issued time.
     * @return the number of rows affected by the save operation.
     */
    fun saveToken(userId: String, tokenResponse: TokenResponse): Int {
        connection.prepareStatement(INSERT_OR_UPDATE_OAUTH_TOKEN).use { statement ->
            statement.setString(1, userId)
            statement.setString(2, tokenResponse.accessToken)
            statement.setString(3, tokenResponse.refreshToken)
            statement.setInt(4, tokenResponse.expiresIn)
            statement.setString(5, tokenResponse.tokenType)
            statement.setString(6, tokenResponse.clientId)
            statement.setLong(7, tokenResponse.issuedAt) // Store the issuedAt value
            return statement.executeUpdate()
        }
    }

    /**
     * Retrieves a token from the database for the specified user ID.
     *
     * @param userId the unique identifier of the user.
     * @return the TokenResponse containing token data, or null if no token is found for the user.
     */
    fun getToken(userId: String): TokenResponse? {
        connection.prepareStatement(SELECT_OAUTH_TOKEN_BY_USER_ID).use { statement ->
            statement.setString(1, userId)
            statement.executeQuery().use { resultSet ->
                return if (resultSet.next()) {
                    TokenResponse(
                        accessToken = resultSet.getString("access_token"),
                        refreshToken = resultSet.getString("refresh_token"),
                        expiresIn = resultSet.getInt("expires_in"),
                        tokenType = resultSet.getString("token_type"),
                        clientId = resultSet.getString("client_id"),
                        userId = resultSet.getString("user_id"),
                        issuedAt = resultSet.getLong("issued_at") // Retrieve the issuedAt value
                    )
                } else null
            }
        }
    }

    /**
     * Deletes a token from the database for the specified user ID.
     *
     * @param userId the unique identifier of the user.
     * @return the number of rows affected by the delete operation.
     */
    fun deleteToken(userId: String): Int {
        connection.prepareStatement("DELETE FROM oauth_tokens WHERE user_id = ?").use { statement ->
            statement.setString(1, userId)
            return statement.executeUpdate()
        }
    }
}