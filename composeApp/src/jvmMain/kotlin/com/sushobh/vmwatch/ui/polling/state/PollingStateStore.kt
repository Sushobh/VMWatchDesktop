package com.sushobh.vmwatch.ui.polling.state

import com.sushobh.vmwatch.FLParserApiResponse
import com.sushobh.vmwatch.FLProperty
import com.sushobh.vmwatch.FLViewModelId
import com.sushobh.vmwatch.ui.polling.PollingVMConnectionState
import com.sushobh.vmwatch.ui.polling.PollingVMFieldValueState
import com.sushobh.vmwatch.ui.polling.PollingVMVmDetailsState
import com.sushobh.vmwatch.ui.polling.PollingVMVmListState

// 1. State
data class FLPollingState(
    val connectionState: PollingVMConnectionState = PollingVMConnectionState.NotConnected,
    val listState: PollingVMVmListState = PollingVMVmListState.Loading,
    val detailsState: PollingVMVmDetailsState = PollingVMVmDetailsState.Waiting,
    val fieldState: PollingVMFieldValueState = PollingVMFieldValueState.Waiting
)

// 2. Events
sealed interface FLPollingEvent {
    data object FetchVmList : FLPollingEvent
    data class VmListFetched(val vms: List<FLViewModelId>) : FLPollingEvent
    data object VmListFetchFailed : FLPollingEvent

    data class ViewModelClicked(val viewModelId: FLViewModelId) : FLPollingEvent
    data object FetchingVmDetails : FLPollingEvent
    data class VmDetailsFetched(val response: FLParserApiResponse, val selectedId: FLViewModelId) : FLPollingEvent
    data class VmDetailsFetchFailed(val error: String) : FLPollingEvent

    data class MoreDetailsClicked(val property: FLProperty) : FLPollingEvent
    data object FetchingFieldValue : FLPollingEvent
    data class FieldValueFetched(val value: FLProperty) : FLPollingEvent
    data class FieldValueFetchFailed(val error: String) : FLPollingEvent
    data object CloseFieldDetails : FLPollingEvent
}



// 4. Middleware
