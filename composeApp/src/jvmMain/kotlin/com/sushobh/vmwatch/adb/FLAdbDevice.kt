package com.sushobh.vmwatch.adb

import com.android.ddmlib.AdbCommandRejectedException
import com.android.ddmlib.IDevice
import com.android.ddmlib.TimeoutException
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

    suspend fun removePortForward(forwardedPort : Int): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                iDevice.removeForward(forwardedPort)
            }
            Result.success(Unit)
        } catch (e: TimeoutException) {
            Result.failure(IOException("Remove port forward timeout: ${e.message}", e))
        } catch (e: AdbCommandRejectedException) {
            Result.failure(IOException("Remove port forward rejected: ${e.message}", e))
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}