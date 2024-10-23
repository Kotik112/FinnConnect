package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.model.User
import java.util.Date


fun generateToken(user: User, jwtSecret: String): String {
    val expiration = if (user.rememberMe) {
        Date(System.currentTimeMillis() + 60 * 60 * 1000 * 24 * 7) // 7 days
    } else {
        Date(System.currentTimeMillis() + 60 * 60 * 1000) // 1 hour
    }

    return JWT.create()
        .withAudience("FinnConnect")
        .withIssuer("https://www.finnconnect.com/")
        .withClaim("username", user.username)
        .withClaim("role", user.role.name)
        .withExpiresAt(expiration)
        .sign(Algorithm.HMAC256(jwtSecret))
}