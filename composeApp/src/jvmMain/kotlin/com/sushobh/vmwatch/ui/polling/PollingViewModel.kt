package com.sushobh.vmwatch.ui.polling

import com.sushobh.vmwatch.FLParserApiResponse
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

    private val _vmConnectionState = MutableStateFlow<PollingVMConnectionState>(PollingVMConnectionState.NotConnected)
    val connectionState = _vmConnectionState.asStateFlow()
    private val _vmMainState = MutableStateFlow(PollingVMMainState(PollingVMVmListState.Loading,
        PollingVMVmDetailsState.Waiting))
    val vmMainState = _vmMainState


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
                        _vmConnectionState.value = PollingVMConnectionState.Connected
                        _vmMainState.value = _vmMainState.value.copy(listState = PollingVMVmListState.Success(response))
                    } catch (e: Exception) {
                        _vmConnectionState.value = PollingVMConnectionState.NotConnected
                    }
                }
                delay(configApi.getPollingInterval())
            }
        }
    }

    fun onViewModelClicked(viewModelId: FLViewModelId) {
        viewModelScope.launch {
            _vmMainState.value = _vmMainState.value.copy(propertyState = PollingVMVmDetailsState.Loading)
            try {
                val response: FLParserApiResponse = client.post("${configApi.getApiHost()}/getpropsforviewmodel") {
                    contentType(ContentType.Application.Json)
                    setBody(viewModelId)
                }.body()
                if(response.isSuccess){
                    _vmMainState.value = _vmMainState.value.copy(propertyState = PollingVMVmDetailsState.Success(response))
                }
                else {
                    _vmMainState.value = _vmMainState.value.copy(propertyState = PollingVMVmDetailsState.Error("Could not load properties"))
                }
            } catch (e: Exception) {
                _vmMainState.value = _vmMainState.value.copy(propertyState = PollingVMVmDetailsState.Error("Could not load properties"))
            }
        }
    }


}