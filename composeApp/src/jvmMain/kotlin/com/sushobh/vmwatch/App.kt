package com.sushobh.vmwatch

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.sushobh.vmwatch.adb.FLAdbWrapper
import com.sushobh.vmwatch.config.ConfigApi
import com.sushobh.vmwatch.ui.AppBar
import com.sushobh.vmwatch.ui.ViewModelDetails
import com.sushobh.vmwatch.ui.ViewModelList
import com.sushobh.vmwatch.ui.theme.ThemeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    val configApi = remember { ConfigApi() }
    val flAdbWrapper = remember { FLAdbWrapper() }
    val deviceViewModel = remember { DeviceViewModel(flAdbWrapper) }
    val vmListViewModel = remember { VmListViewModel() }
    val themeViewModel = remember { ThemeViewModel() }

    val devices by deviceViewModel.devices.collectAsState()
    val selectedDevice by deviceViewModel.selectedDevice.collectAsState()

    val viewModels by vmListViewModel.viewModels.collectAsState()
    val selectedViewModel by vmListViewModel.selectedViewModel.collectAsState()

    val availableThemes by themeViewModel.themes.collectAsState()
    val currentTheme by themeViewModel.currentTheme.collectAsState()

    MaterialTheme(
        colorScheme = currentTheme.colorScheme
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    appName = configApi.getAppName(),
                    devices = devices,
                    selectedDevice = selectedDevice,
                    availableThemes = availableThemes,
                    selectedTheme = currentTheme,
                    onDeviceSelected = { device ->
                        deviceViewModel.onDeviceSelected(device)
                    },
                    onThemeSelected = { theme -> themeViewModel.onThemeSelected(theme) }
                )
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                ViewModelList(
                    viewModels = viewModels,
                    selectedViewModel = selectedViewModel,
                    onViewModelSelected = { vmName -> vmListViewModel.onViewModelSelected(vmName) }
                )

                VerticalDivider()

                ViewModelDetails(
                    selectedViewModel = selectedViewModel,
                    modifier = Modifier.weight(3f)
                )
            }
        }
    }
}