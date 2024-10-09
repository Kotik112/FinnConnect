package com.example.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

open class HttpClientBase {

    protected val client: HttpClient = HttpClient(CIO) {
        // Configure the CIO engine for connection pooling
        engine {
            //pipelining = true  // Enable HTTP/1.1 pipelining
            maxConnectionsCount = 1000  // Maximum number of connections
            endpoint {
                maxConnectionsPerRoute = 100  // Maximum connections per route
                connectTimeout = 5000  // Connection timeout in milliseconds
                keepAliveTime = 5000  // Keep-alive time for connections in milliseconds
                pipelineMaxSize = 20  // Maximum size of the pipeline
                requestTimeout = 10000  // Request timeout in milliseconds
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
}

