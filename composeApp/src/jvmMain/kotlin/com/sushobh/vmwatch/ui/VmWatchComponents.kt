package com.sushobh.vmwatch.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.sushobh.vmwatch.ui.polling.PollingViewModel
import com.sushobh.vmwatch.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    appName: String,
    devices: List<String>,
    selectedDevice: String?,
    availableThemes: List<AppTheme>,
    selectedTheme: AppTheme,
    onDeviceSelected: (String) -> Unit,
    onThemeSelected: (AppTheme) -> Unit
) {
    TopAppBar(
        title = { Text(appName, style = MaterialTheme.typography.titleLarge) },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                  ThemeSelector(availableThemes, selectedTheme, onThemeSelected)
                 //DeviceSelector(devices, selectedDevice, onDeviceSelected)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ThemeSelector(
    availableThemes: List<AppTheme>,
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    var themeExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = themeExpanded,
        onExpandedChange = { themeExpanded = !themeExpanded }
    ) {
        TextField(
            value = selectedTheme.displayName,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = themeExpanded) },
            modifier = Modifier.menuAnchor(),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        ExposedDropdownMenu(
            expanded = themeExpanded,
            onDismissRequest = { themeExpanded = false }
        ) {
            availableThemes.forEach { theme ->
                DropdownMenuItem(
                    text = { Text(theme.displayName) },
                    onClick = {
                        onThemeSelected(theme)
                        themeExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DeviceSelector(
    devices: List<String>,
    selectedDevice: String?,
    onDeviceSelected: (String) -> Unit
) {
    var deviceExpanded by remember { mutableStateOf(false) }
    val selectedDeviceText = selectedDevice ?: "No devices"

    ExposedDropdownMenuBox(
        expanded = deviceExpanded,
        onExpandedChange = { deviceExpanded = !deviceExpanded }
    ) {
        TextField(
            value = selectedDeviceText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = deviceExpanded) },
            modifier = Modifier.menuAnchor(),
            colors = androidx.compose.material3.TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        ExposedDropdownMenu(
            expanded = deviceExpanded,
            onDismissRequest = { deviceExpanded = false }
        ) {
            if (devices.isNotEmpty()) {
                devices.forEach { device ->
                    DropdownMenuItem(
                        text = { Text(device) },
                        onClick = {
                            onDeviceSelected(device)
                            deviceExpanded = false
                        }
                    )
                }
            } else {
                DropdownMenuItem(
                    text = { Text("No devices found") },
                    onClick = { deviceExpanded = false },
                    enabled = false
                )
            }
        }
    }
}




