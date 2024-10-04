package com.example.service

import com.example.dao.OAuthTokenRepository
import com.example.model.TokenResponse
import com.example.utils.TimeProvider
import org.slf4j.LoggerFactory

/**
 * Service for managing OAuth tokens, providing methods for saving, retrieving,
 * checking expiration, and deleting tokens.
 *
 * @property tokenRepository the repository for accessing token data.
 * @property timeProvider a utility for getting the current time with a specified time zone.
 */
class OAuthTokenService(
    private val tokenRepository: OAuthTokenRepository,
    private val timeProvider: TimeProvider
) {
    private val logger = LoggerFactory.getLogger(OAuthTokenService::class.java)

    /**
     * Saves a token using the repository. If a token already exists for the user,
     * it will be updated.
     *
     * @param tokenResponse the token data to be saved.
     * @return true if the token was saved successfully, false otherwise.
     */
    fun saveToken(tokenResponse: TokenResponse): Boolean {
        return try {
            val rowsAffected = tokenRepository.saveToken(tokenResponse.userId, tokenResponse)
            logger.info("Token saved for user: ${tokenResponse.userId}, Rows affected: $rowsAffected")
            rowsAffected > 0
        } catch (e: Exception) {
            logger.error("Error saving token for user: ${tokenResponse.userId}", e)
            false
        }
    }

    /**
     * Retrieves a token for the specified user ID.
     *
     * @param userId the unique identifier of the user.
     * @return the TokenResponse containing token data, or null if no token is found.
     */
    fun getToken(userId: String): TokenResponse? {
        return try {
            tokenRepository.getToken(userId)?.also { _ ->
                logger.info("Retrieved token for user: $userId")
            }
        } catch (e: Exception) {
            logger.error("Error retrieving token for user: $userId", e)
            null
        }
    }

    /**
     * Checks whether a token has expired based on its issued time and expiration period.
     *
     * @param token the token data to be checked for expiration.
     * @return true if the token has expired, false otherwise.
     */
    fun isTokenExpired(token: TokenResponse): Boolean {
        val expirationTime = token.issuedAt + token.expiresIn
        val currentTimeEpoch = timeProvider.getCurrentTime().atZone(timeProvider.zoneId).toEpochSecond()
        return currentTimeEpoch >= expirationTime
    }

    /**
     * Deletes a token for the specified user ID.
     *
     * @param userId the unique identifier of the user.
     * @return true if the token was deleted successfully, false otherwise.
     */
    fun deleteToken(userId: String): Boolean {
        return try {
            val rowsAffected = tokenRepository.deleteToken(userId)
            logger.info("Deleted token for user: $userId, Rows affected: $rowsAffected")
            rowsAffected > 0
        } catch (e: Exception) {
            logger.error("Error deleting token for user: $userId", e)
            false
        }
    }
}
