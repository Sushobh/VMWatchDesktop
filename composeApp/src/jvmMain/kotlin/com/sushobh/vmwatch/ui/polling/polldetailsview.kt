package com.sushobh.vmwatch.ui.polling

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sushobh.vmwatch.FLProperty
import com.sushobh.vmwatch.FLPropertyOwner
import com.sushobh.vmwatch.ui.PropertyRow
import kotlinx.coroutines.flow.map

@Composable
fun FLPollingDetailsView(pollingViewModel: PollingViewModel) {
    val connectionState = pollingViewModel.connectionState.collectAsState()
    val vmListState = pollingViewModel.vmMainState.map { it.listState }.collectAsState(PollingVMVmListState.Loading)
    val vmDetailsState = pollingViewModel.vmMainState.map { it.propertyState }.collectAsState(PollingVMVmDetailsState.Loading)
    Column(modifier = Modifier.fillMaxSize()) {
        when (connectionState.value) {
            PollingVMConnectionState.Connected -> {
                Row {
                    Box(modifier = Modifier.weight(1f)) {
                        ViewModelList(vmListState.value,pollingViewModel)
                    }
                    Box(modifier = Modifier.weight(4f)) {
                        ViewModelDetails(vmDetailsState.value,pollingViewModel)
                    }
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

@Composable
fun ViewModelDetails(state : PollingVMVmDetailsState,viewModel: PollingViewModel) {

    Card(
        modifier = Modifier.fillMaxSize() .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
               ,
            contentAlignment = Alignment.Center
        ) {
            when(state){
                is PollingVMVmDetailsState.Error -> {
                    Text("Error")
                }
                PollingVMVmDetailsState.Loading -> {
                    Text("Loading....")
                }
                is PollingVMVmDetailsState.Success -> {

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(modifier = Modifier.fillMaxWidth().padding(10.dp), text = state.vmDetails.viewmodelName)
                        }

                        items(state.vmDetails.items) { property ->
                            PropertyRow(property, onClick = {

                            })
                            Divider(color = androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        }
                    }
                }
                PollingVMVmDetailsState.Waiting -> {
                    Text("Please select a viewmodel")
                }
            }
        }

    }
}


@Composable
fun ViewModelList(listState: PollingVMVmListState,viewModel: PollingViewModel) {
    Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
        when(listState){
            is PollingVMVmListState.Error -> {
                Text("Something's wrong")
            }
            PollingVMVmListState.Loading -> {
                  Text("Loading")
            }
            is PollingVMVmListState.Success -> {
                LazyColumn(modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.surface).fillMaxSize()) {

                    items(listState.vmList) { vm ->
                        ViewModelItem(
                            name = vm.name,
                            onClick = { viewModel.onViewModelClicked(vm) },
                            isSelected = vm == listState.selectedId
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun ViewModelItem(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = if (isSelected) androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer else androidx.compose.material3.MaterialTheme.colorScheme.onSurface
    )
}