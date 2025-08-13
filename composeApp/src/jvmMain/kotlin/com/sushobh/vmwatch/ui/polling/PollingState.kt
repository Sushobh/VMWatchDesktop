package com.sushobh.vmwatch.ui.polling

import com.sushobh.vmwatch.FLPropertyOwner
import com.sushobh.vmwatch.FLViewModelId

sealed class PollingVMVmListState {
    data object Loading : PollingVMVmListState()
    data class  Success(val vmList: List<FLViewModelId>) : PollingVMVmListState()
    data class  Error(val message: String) : PollingVMVmListState()
}

sealed interface PollingVMConnectionState {
    data object Connected : PollingVMConnectionState
    data object NotConnected : PollingVMConnectionState
}

sealed class PollingVMVmDetailsState {
    data object Loading : PollingVMVmDetailsState()
    data class Success(val vmDetails: FLPropertyOwner) : PollingVMVmDetailsState()
    data class Error(val message: String) : PollingVMVmDetailsState()
    data object Waiting : PollingVMVmDetailsState()
}
