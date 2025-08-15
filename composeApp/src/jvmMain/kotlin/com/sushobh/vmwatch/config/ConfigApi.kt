package com.sushobh.vmwatch.config

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ConfigApi {

    sealed interface FLPlatform {
        data object Windows : FLPlatform
        data object Mac : FLPlatform
    }

    fun getAppName(): String = "FragLens Desktop"
    fun getPlatForm(): FLPlatform {
        val os = System.getProperty("os.name").lowercase()
        return when {
            os.contains("win") -> FLPlatform.Windows
            os.contains("mac") -> FLPlatform.Mac
            else -> FLPlatform.Mac
        }
    }

    fun getDevicePort() : Int {
        return 56440
    }

    fun allowedPorts() = intArrayOf(56441,56442,56443,56444,56445)

    fun getApiHost() = "http://localhost:56441"

    fun getPollingInterval() = 2000L // 5 seconds

    val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }
}