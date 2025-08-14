package com.sushobh.vmwatch.ui.polling

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FLPollingDetailsView(pollingViewModel: PollingViewModel) {
    val connectionState = pollingViewModel.connectionState.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        when (connectionState.value) {
            PollingVMConnectionState.Connected -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Connection successfull!",
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.primary
                    )
                }
            }

            PollingVMConnectionState.NotConnected -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Not connected, please check adb connection.",
                        style = MaterialTheme.typography.h3,
                        color = MaterialTheme.colors.error
                    )
                }
            }
        }
    }

}