package com.sushobh.vmwatch

import com.sushobh.vmwatch.adb.FLAdbEvent
import com.sushobh.vmwatch.adb.FLAdbWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DeviceViewModel(
    private val flAdbWrapper: FLAdbWrapper,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {

    private val _devices = MutableStateFlow<List<String>>(emptyList())
    val devices: StateFlow<List<String>> = _devices.asStateFlow()

    private val _selectedDevice = MutableStateFlow<String?>(null)
    val selectedDevice: StateFlow<String?> = _selectedDevice.asStateFlow()

    init {
        loadDevices()
    }

    fun onDeviceSelected(device: String) {
        _selectedDevice.value = device
    }

    private fun loadDevices() {
        coroutineScope.launch {
            flAdbWrapper.observeDevices()
                .collect { event ->
                    when (event) {
                        is FLAdbEvent.DevicesConnected -> {
                            val deviceSerials = event.devices.map { it.serialNumber }
                            _devices.value = deviceSerials
                            if (_selectedDevice.value == null || _selectedDevice.value !in deviceSerials) {
                                _selectedDevice.value = deviceSerials.firstOrNull()
                            }
                        }
                        is FLAdbEvent.NoDevices -> {
                            _devices.value = emptyList()
                            _selectedDevice.value = null
                        }
                        is FLAdbEvent.Error -> {
                            // TODO: Expose error to the UI
                            _devices.value = emptyList()
                            _selectedDevice.value = null
                        }
                    }
                }
        }
    }
}