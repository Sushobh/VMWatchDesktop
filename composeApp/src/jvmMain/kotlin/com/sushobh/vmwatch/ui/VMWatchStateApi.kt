package com.sushobh.vmwatch.ui

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class PollingControlState {
    object PollingPaused : PollingControlState()
    object PollingResumed : PollingControlState()
}

interface VMWatchStateApi {
    val pollingControlState: StateFlow<PollingControlState>
    fun pausePolling()
    fun resumePolling()
}

class VMWatchStateApiImpl : VMWatchStateApi {
    private val _pollingControlState = MutableStateFlow<PollingControlState>(PollingControlState.PollingResumed)
    override val pollingControlState: StateFlow<PollingControlState> = _pollingControlState

    override fun pausePolling() {
        _pollingControlState.value = PollingControlState.PollingPaused
    }

    override fun resumePolling() {
        _pollingControlState.value = PollingControlState.PollingResumed
    }
}
