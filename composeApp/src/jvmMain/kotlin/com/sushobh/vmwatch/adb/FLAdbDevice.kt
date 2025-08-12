package com.sushobh.vmwatch.adb

import com.android.ddmlib.AdbCommandRejectedException
import com.android.ddmlib.IDevice
import com.android.ddmlib.TimeoutException
import com.sushobh.vmwatch.config.ConfigApi
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException


class FLAdbDevice(private val iDevice: IDevice) {
    val name: String
        get() = iDevice.name
    val serial: String
        get() = iDevice.serialNumber
    val isEmulator: Boolean
        get() = iDevice.isEmulator
    val isOnline: Boolean
        get() = iDevice.isOnline
    private var devicePort : Int? = null
    private var computerPort : Int? = null

    sealed class FLAdbConnectionState {
        object NotConnected : FLAdbConnectionState()
        data class Connected(val port: Int) : FLAdbConnectionState()
        data class Error(val message: String) : FLAdbConnectionState()
    }

}