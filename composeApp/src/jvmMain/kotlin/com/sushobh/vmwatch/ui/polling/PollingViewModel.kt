package com.sushobh.vmwatch.ui.polling

import com.sushobh.libs.com.sushobh.libs.sbstate.Middleware
import com.sushobh.libs.com.sushobh.libs.sbstate.StateStore
import com.sushobh.libs.com.sushobh.libs.sbstate.Store
import com.sushobh.vmwatch.FLCListViewItem
import com.sushobh.vmwatch.FLParserApiResponse
import com.sushobh.vmwatch.FLSerializeFieldResponse
import com.sushobh.vmwatch.FLViewModelId
import com.sushobh.vmwatch.config.ConfigApi
import com.sushobh.vmwatch.ui.PollingControlState
import com.sushobh.vmwatch.ui.VMWatchStateApi
import com.sushobh.vmwatch.ui.polling.state.FLPollingEvent
import com.sushobh.vmwatch.ui.polling.state.FLPollingState
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PollingViewModel(
    private val configApi: ConfigApi,
    private val vmWatchStateApi: VMWatchStateApi
) : Store<FLPollingState, FLPollingEvent> {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)


    private val store = StateStore(
        initialState = FLPollingState(),
        reducerFn = ::reducer,
        middlewares = listOf(createPollingMiddleware(configApi, viewModelScope)),
        coroutineScope = viewModelScope
    )


    override val state: StateFlow<FLPollingState> = store.state

    val listItems = state.map { it.listState }.filterIsInstance<PollingVMVmListState.Success>()
        .map { createListResponse(it.vmList) }

    override fun dispatch(event: FLPollingEvent) {
        store.dispatch(event)
    }

    override fun reducer(
        state: FLPollingState,
        event: FLPollingEvent
    ): FLPollingState {
        return when (event) {
            is FLPollingEvent.VmListFetched -> state.copy(
                connectionState = PollingVMConnectionState.Connected,
                listState = PollingVMVmListState.Success(
                    event.vms,
                    selectedId = if (state.listState is PollingVMVmListState.Success) {
                        state.listState.selectedId
                    } else null
                )
            )

            is FLPollingEvent.VmListFetchFailed -> state.copy(
                connectionState = PollingVMConnectionState.NotConnected,
                detailsState = PollingVMVmDetailsState.Waiting,
                fieldState = PollingVMFieldValueState.Waiting
            )

            is FLPollingEvent.FetchingVmDetails -> state.copy(
                detailsState = PollingVMVmDetailsState.Loading
            )

            is FLPollingEvent.VmDetailsFetched -> {
                state.copy(detailsState = PollingVMVmDetailsState.Success(event.response),)
            }

            is FLPollingEvent.VmDetailsFetchFailed -> state.copy(
                detailsState = PollingVMVmDetailsState.Error(event.error),
                fieldState = PollingVMFieldValueState.Waiting
            )

            is FLPollingEvent.FetchingFieldValue -> state.copy(
                fieldState = PollingVMFieldValueState.Loading
            )

            is FLPollingEvent.FieldValueFetched -> state.copy(
                fieldState = PollingVMFieldValueState.Success(event.value)
            )

            is FLPollingEvent.FieldValueFetchFailed -> state.copy(
                fieldState = PollingVMFieldValueState.Error(event.error)
            )

            is FLPollingEvent.ViewModelClicked -> {
                if(state.listState is PollingVMVmListState.Success){
                    state.copy(fieldState = PollingVMFieldValueState.Waiting, listState = state.listState.copy(selectedId = event.viewModelId))
                }
                else {
                    state.copy(fieldState = PollingVMFieldValueState.Waiting)
                }

            }

            is FLPollingEvent.CloseFieldDetails -> state.copy(fieldState = PollingVMFieldValueState.Waiting)

            else -> state
        }
    }

    init {
        startPolling()
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (true) {
                if (vmWatchStateApi.pollingControlState.value is PollingControlState.PollingResumed) {
                    store.dispatch(FLPollingEvent.FetchVmList)
                }
                delay(configApi.getPollingInterval())
            }
        }
    }

    private fun createListResponse(list: List<FLViewModelId>): List<FLCListViewItem> {
        val owners: Map<Int, List<FLViewModelId>> = list.groupBy { it.ownerCode }
        val viewList: ArrayList<FLCListViewItem> = arrayListOf<FLCListViewItem>()
        owners.forEach {
            val values = it.value.filter { it.name != "SavedStateHandlesVM" }
            if (values.isEmpty()) {
                return@forEach
            } else {
                val anyItem = values.first()
                val ownerName = anyItem.ownerName
                val ownerCode = anyItem.ownerCode
                viewList.add(FLCListViewItem.FLCListViewModelOwner(name = ownerName, code = ownerCode.toString()))
                values.forEach {
                    viewList.add(FLCListViewItem.FLCListViewModel(it, "${ownerCode}/${it.code}"))
                }
            }

        }
        return viewList
    }

    fun createPollingMiddleware(
        configApi: ConfigApi,
        coroutineScope: CoroutineScope
    ): Middleware<FLPollingState, FLPollingEvent> {
        val client = configApi.httpClient

        return { _, event, dispatch ->

            when (event) {
                is FLPollingEvent.FetchVmList -> {
                    coroutineScope.launch {
                        try {
                            val response: List<FLViewModelId> =
                                client.get("${configApi.getApiHost()}/getallviewmodels").body()
                            dispatch(FLPollingEvent.VmListFetched(response))
                        } catch (e: Exception) {
                            dispatch(FLPollingEvent.VmListFetchFailed)
                        }
                    }
                }

                is FLPollingEvent.ViewModelClicked -> {
                    coroutineScope.launch {
                        dispatch(FLPollingEvent.FetchingVmDetails)
                        try {
                            val response: FLParserApiResponse =
                                client.post("${configApi.getApiHost()}/getpropsforviewmodel") {
                                    contentType(ContentType.Application.Json)
                                    setBody(event.viewModelId)
                                }.body()
                            if (response.isSuccess) {
                                dispatch(FLPollingEvent.VmDetailsFetched(response, event.viewModelId))
                            } else {
                                dispatch(FLPollingEvent.VmDetailsFetchFailed("Could not load properties"))
                            }
                        } catch (e: Exception) {
                            dispatch(FLPollingEvent.VmDetailsFetchFailed("Could not load properties"))
                        }
                    }
                }

                is FLPollingEvent.MoreDetailsClicked -> {
                    coroutineScope.launch {
                        dispatch(FLPollingEvent.FetchingFieldValue)
                        try {
                            val response: FLSerializeFieldResponse =
                                client.post("${configApi.getApiHost()}/getdetailsfromprop") {
                                    contentType(ContentType.Application.Json)
                                    setBody(event.property.refPath)
                                }.body()
                            if (response.isSuccess && response.value != null) {
                                dispatch(FLPollingEvent.FieldValueFetched(response.value))
                            } else {
                                dispatch(FLPollingEvent.FieldValueFetchFailed("Could not load field value"))
                            }
                        } catch (e: Exception) {
                            dispatch(FLPollingEvent.FieldValueFetchFailed("Could not load field value"))
                        }
                    }
                }

                else -> Unit // Do nothing for other events
            }
        }
    }


}
