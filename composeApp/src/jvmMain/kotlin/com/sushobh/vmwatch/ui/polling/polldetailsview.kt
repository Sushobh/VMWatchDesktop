package com.sushobh.vmwatch.ui.polling

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sushobh.vmwatch.FLCListViewItem
import com.sushobh.vmwatch.FLProperty
import com.sushobh.vmwatch.common.SvgIconButton
import com.sushobh.vmwatch.ui.json.PrettyJsonView
import com.sushobh.vmwatch.ui.polling.state.FLPollingEvent
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_close
import kotlinx.coroutines.flow.map

@Composable
fun FLPollingDetailsView(pollingViewModel: PollingViewModel) {
    val connectionState by pollingViewModel.state.map { it.connectionState }.collectAsState(PollingVMConnectionState.NotConnected)
    val listState by pollingViewModel.state.map { it.listState }.collectAsState(PollingVMVmListState.Loading)
    val detailState  by pollingViewModel.state.map { it.detailsState }.collectAsState(
        PollingVMVmDetailsState.Waiting)
    val fieldState  by pollingViewModel.state.map { it.fieldState }.collectAsState(
        PollingVMFieldValueState.Waiting)

    Column(modifier = Modifier.fillMaxSize()) {

        when (connectionState) {
            PollingVMConnectionState.Connected -> {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f)) {
                        ViewModelList(listState, pollingViewModel)
                    }
                    Box(modifier = Modifier.weight(4f)) {
                        ViewModelDetails(detailState ,fieldState,pollingViewModel)
                    }
                }
            }
            PollingVMConnectionState.NotConnected -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Not connected, please check adb connection.",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun ViewModelDetails(state: PollingVMVmDetailsState, fieldState: PollingVMFieldValueState, viewModel: PollingViewModel) {

    Card(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            when(state) {
                PollingVMVmDetailsState.Loading  -> {
                    Text("Loading....")
                }
                is PollingVMVmDetailsState.Error -> {
                    Text(state.message)
                }
                is PollingVMVmDetailsState.Success -> {
                    Row(modifier = Modifier.fillMaxSize()){
                        LazyColumn(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.65f)) {
                            item {
                                Text(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    text = state.vmDetails.viewmodelName,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            items(state.vmDetails.items) { property ->
                                PropertyRow(property, onClick = {
                                    viewModel.dispatch(FLPollingEvent.MoreDetailsClicked(it))
                                })
                                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            }
                        }

                        if(fieldState is PollingVMFieldValueState.Success){
                            val vmName = if(state is PollingVMVmDetailsState.Success){
                                state.vmDetails.viewmodelName
                            }
                            else {
                                ""
                            }
                            Column(modifier = Modifier.fillMaxHeight()) {
                                FLCFieldDetails(fieldState.fieldDetails,vmName,{
                                    viewModel.dispatch(FLPollingEvent.CloseFieldDetails)
                                })
                            }
                        }

                    }
                }
                else -> {
                    Text("Please select a viewmodel from the list.")
                }
            }
        }
    }
}
@Composable
fun FLCFieldDetails(field: FLProperty, vmName: String,onClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top-right close button
        Box(modifier = Modifier.fillMaxWidth()) {
            SvgIconButton(
                onClick = { onClick() },
                path = Res.drawable.ic_close,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            )
        }
        Text("${vmName} -> ${field.name}", modifier = Modifier,style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(30.dp))

        PrettyJsonView(field.fieldValue.orEmpty())
    }
}

@Composable
fun ViewModelList(state: PollingVMVmListState,viewModel: PollingViewModel) {

    val items = viewModel.listItems.collectAsState(emptyList())
    var toggledOwnerItems : HashSet<String> = remember {
        hashSetOf()
    }
    Column(
        modifier = Modifier.fillMaxHeight().background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when(state) {
            PollingVMVmListState.Loading  -> {
                Text("Loading", modifier = Modifier.padding(16.dp))
            }
            is PollingVMVmListState.Error -> {
                Text(state.message, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.error)
            }
            is PollingVMVmListState.Success ->
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val listItems = items.value
                    items(listItems, key = { it.code }) { vm ->
                        when(vm){
                            is FLCListViewItem.FLCListViewModelOwner -> {
                                FLCViewModelOwner(vm,toggledOwnerItems.contains(vm.code), {
                                      if(toggledOwnerItems.contains(vm.code)){
                                          toggledOwnerItems = toggledOwnerItems.toHashSet().also { it.remove(vm.code) }
                                      }
                                      else {
                                          toggledOwnerItems = toggledOwnerItems.toHashSet().also { it.add(vm.code) }
                                      }
                                })
                            }
                            is FLCListViewItem.FLCListViewModel -> {
                                FLCViewModelItem(
                                    name = vm.viewModelId.name,
                                    onClick = {
                                        viewModel.dispatch(FLPollingEvent.ViewModelClicked(vm.viewModelId))
                                    },
                                    isSelected = vm.viewModelId.code == state.selectedId?.code
                                )
                            }
                        }

                    }
                }
        }
    }
}

@Composable
fun FLCViewModelOwner(item : FLCListViewItem.FLCListViewModelOwner, isToggledOn : Boolean, onClick: () -> Unit) {
    Text(
        text = item.name,
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isToggledOn) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = if (isToggledOn) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.headlineSmall
    )
}


@Composable
fun FLCViewModelItem(name: String, isSelected: Boolean, onClick: () -> Unit) {
    Text(
        text = name,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
            .padding(start = 24.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
fun PropertyRow(property: FLProperty, onClick: (property: FLProperty) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(property) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = property.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = property.value ?: "null",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

