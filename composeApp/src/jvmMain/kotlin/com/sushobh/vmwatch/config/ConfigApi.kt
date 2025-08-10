package com.sushobh.vmwatch.config

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

}