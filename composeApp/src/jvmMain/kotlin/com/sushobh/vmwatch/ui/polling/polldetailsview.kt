package com.sushobh.vmwatch.ui.polling

import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.sushobh.vmwatch.ui.ViewModelDetails
import com.sushobh.vmwatch.ui.ViewModelList

@Composable
fun FLPollingDetailsView(pollingViewModel: PollingViewModel) {
    val connectionState = pollingViewModel.connectionState.collectAsState()
    when(connectionState.value){
        PollingVMConnectionState.Connected -> {

        }
        PollingVMConnectionState.NotConnected -> {

        }
    }
}