package com.sushobh.vmwatch

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.sushobh.vmwatch.config.ConfigApi

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = ConfigApi().getAppName(),
    ) {
        App()
    }
}