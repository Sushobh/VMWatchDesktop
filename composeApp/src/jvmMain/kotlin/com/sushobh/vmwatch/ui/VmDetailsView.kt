package com.sushobh.vmwatch.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.sushobh.vmwatch.FLProperty
import com.sushobh.vmwatch.ui.polling.PollingViewModel

const val MAX_DISPLAYABLE_LENGTH = 100

@Composable
fun VmDetailsView(viewModel: PollingViewModel) {
    val dummyProperties = (1..100).map { i ->
        val isLong = i % 5 == 0
        val isNull = i % 10 == 0
        FLProperty(
            name = "property$i",
            type = "Type$i",
            value = when {
                isNull -> null
                isLong -> "long string value ".repeat(20)
                else -> "value $i"
            }
        )
    }

    // Aligning with ViewModelList by using MaterialTheme.colorScheme.surface
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(dummyProperties) { property ->
                PropertyRow(property, onClick = {
                    viewModel.onShowFullContentClicked(property)
                })
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }
        }
    }
}

@Composable
fun PropertyRow(property: FLProperty, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Using onSurface for primary text, consistent with ViewModelItem
        Text(
            text = property.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (property.value != null && property.value.length > MAX_DISPLAYABLE_LENGTH) {
            Text(
                text = "Too big to display inline, click to show",
                modifier = Modifier
                    .weight(2f)
                    .clickable(onClick = onClick),
                style = MaterialTheme.typography.bodyMedium.copy(textDecoration = TextDecoration.Underline),
                color = MaterialTheme.colorScheme.secondary
            )
        } else {
            // Using onSurfaceVariant for secondary/less prominent text is appropriate here
            Text(
                text = property.value ?: "null",
                modifier = Modifier.weight(2f),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
