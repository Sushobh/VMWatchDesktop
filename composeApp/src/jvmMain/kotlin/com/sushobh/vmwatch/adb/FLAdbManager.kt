package com.sushobh.vmwatch.adb

import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.TimeoutException
import com.android.ddmlib.AdbCommandRejectedException
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import java.io.IOException
import java.util.concurrent.TimeUnit

sealed class FLAdbEvent {
    data class DevicesConnected(val devices: List<FLAdbDevice>) : FLAdbEvent()
    object NoDevices : FLAdbEvent()
    data class Error(val message: String) : FLAdbEvent()
}



class FLAdbWrapper(private val adbPath: String = "adb") {

    private var bridge: AndroidDebugBridge? = null

    fun observeDevices(): Flow<FLAdbEvent> = callbackFlow {
        try {
            AndroidDebugBridge.init(false)
            bridge = AndroidDebugBridge.createBridge(adbPath, false,5000, TimeUnit.SECONDS)

            if (bridge == null) {
                trySend(FLAdbEvent.Error("ADB bridge could not be created. Check adb path."))
                close()
                return@callbackFlow
            }

            // Wait a little for initial device list
            repeat(10) {
                if (bridge!!.hasInitialDeviceList()) return@repeat
                delay(200)
            }

            val listener = object : AndroidDebugBridge.IDeviceChangeListener {
                override fun deviceConnected(device: IDevice) {
                    trySend(getCurrentDevicesEvent())
                }

                override fun deviceDisconnected(device: IDevice) {
                    trySend(getCurrentDevicesEvent())
                }

                override fun deviceChanged(device: IDevice, changeMask: Int) {
                    trySend(getCurrentDevicesEvent())
                }
            }

            AndroidDebugBridge.addDeviceChangeListener(listener)

            // Emit initial list
            trySend(getCurrentDevicesEvent())

            awaitClose {
                AndroidDebugBridge.removeDeviceChangeListener(listener)
                AndroidDebugBridge.terminate()
            }
        } catch (e: Exception) {
            trySend(FLAdbEvent.Error("ADB error: ${e.message}"))
            close(e)
        }
    }

    private fun getCurrentDevicesEvent(): FLAdbEvent {
        val devices = bridge?.devices?.toList().orEmpty()
        return if (devices.isEmpty()) FLAdbEvent.NoDevices else FLAdbEvent.DevicesConnected(devices.map { FLAdbDevice(it) })
    }

    suspend fun forwardPort(
        device: IDevice,
        local: Int,
        remote: Int
    ): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                device.createForward(local, remote)
            }
            Result.success(Unit)
        } catch (e: TimeoutException) {
            Result.failure(IOException("Port forward timeout: ${e.message}", e))
        } catch (e: AdbCommandRejectedException) {
            Result.failure(IOException("Port forward rejected: ${e.message}", e))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
