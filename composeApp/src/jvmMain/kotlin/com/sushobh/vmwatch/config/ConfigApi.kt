package com.sushobh.vmwatch.config

class ConfigApi {

    sealed interface FLPlatform {
        data object Windows : FLPlatform
        data object Mac : FLPlatform
    }

    fun getPlatForm() {

    }

}