package com.sushobh.vmwatch.common

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.ic_close
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalResourceApi::class)
@Composable
fun SvgIconButton(onClick: () -> Unit,path : DrawableResource,modifier: Modifier) {
    IconButton(onClick = onClick,modifier = modifier) {
        Icon(
            painter = painterResource(path),
            contentDescription = "Click me",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}