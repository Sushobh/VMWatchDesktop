package com.sushobh.vmwatch.ui.polling

import com.sushobh.vmwatch.FLParserApiResponse
import com.sushobh.vmwatch.FLPropertyOwner
import com.sushobh.vmwatch.FLViewModelId

sealed class PollingVMVmListState {
    data object Loading : PollingVMVmListState()
    data class  Success(val vmList: List<FLViewModelId>,val selectedId : FLViewModelId? = null) : PollingVMVmListState()
    data class  Error(val message: String) : PollingVMVmListState()
}

sealed interface PollingVMConnectionState {
    data object Connected : PollingVMConnectionState
    data object NotConnected : PollingVMConnectionState
}

sealed class PollingVMVmDetailsState {
    data object Loading : PollingVMVmDetailsState()
    data class Success(val vmDetails: FLParserApiResponse) : PollingVMVmDetailsState()
    data class Error(val message: String) : PollingVMVmDetailsState()
    data object Waiting : PollingVMVmDetailsState()
}

data class PollingVMMainState(val listState : PollingVMVmListState,val propertyState : PollingVMVmDetailsState)
