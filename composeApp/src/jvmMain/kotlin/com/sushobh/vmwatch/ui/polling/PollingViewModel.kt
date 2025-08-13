package com.sushobh.vmwatch.ui.polling

import com.sushobh.vmwatch.FLProperty
import com.sushobh.vmwatch.FLPropertyOwner
import com.sushobh.vmwatch.FLViewModelId
import com.sushobh.vmwatch.config.ConfigApi
import com.sushobh.vmwatch.ui.PollingControlState
import com.sushobh.vmwatch.ui.VMWatchStateApi
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PollingViewModel(
    private val configApi: ConfigApi,
    private val vmWatchStateApi: VMWatchStateApi
) {

    private val client = configApi.httpClient

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    private val _vmListState = MutableStateFlow<PollingVMVmListState>(PollingVMVmListState.Loading)
    val vmListState: StateFlow<PollingVMVmListState> = _vmListState.asStateFlow()

    private val _vmDetailsState = MutableStateFlow<PollingVMVmDetailsState?>(null)
    val vmDetailsState: StateFlow<PollingVMVmDetailsState?> = _vmDetailsState.asStateFlow()

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                if (vmWatchStateApi.pollingControlState.value is PollingControlState.PollingResumed) {
                    try {
                        val response: List<FLViewModelId> =
                            client.get("${configApi.getApiHost()}/getallviewmodels").body()
                        _vmListState.value = PollingVMVmListState.Success(response)
                    } catch (e: Exception) {
                        _vmListState.value = PollingVMVmListState.Error(e.message ?: "Unknown error")
                    }
                }
                delay(configApi.getPollingInterval())
            }
        }
    }

    fun onViewModelClicked(viewModelId: FLViewModelId) {
        viewModelScope.launch {
            _vmDetailsState.value = PollingVMVmDetailsState.Loading
            try {
                val response: FLPropertyOwner = client.post("${configApi.getApiHost()}/getpropsforviewmodel") {
                    contentType(ContentType.Application.Json)
                    setBody(viewModelId)
                }.body()
                _vmDetailsState.value = PollingVMVmDetailsState.Success(response)
            } catch (e: Exception) {
                _vmDetailsState.value = PollingVMVmDetailsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun onShowFullContentClicked(property: FLProperty) {
        // TODO: Implement a better way to show the full content, like a dialog.
        println("Full content for ${property.name}:")
        println(property.value)
    }
}