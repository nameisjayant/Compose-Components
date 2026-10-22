package com.nameisjayant.composecomponents.catalog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nameisjayant.composecomponents.ui.components.AppIcons
import com.nameisjayant.composecomponents.ui.components.CardShape
import com.nameisjayant.composecomponents.ui.components.DemoTopBar
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

@Composable
fun CatalogScreen(
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onOpen: (ComponentEntry) -> Unit,
) {
    val entries = ComponentEntry.entries
    val ready = entries.count { it.available }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            DemoTopBar(title = "", isDark = isDark, onToggleTheme = onToggleTheme)
            LazyColumn(
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 24.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(Modifier.padding(start = 4.dp, bottom = 20.dp)) {
                        Text(
                            text = "COMPOSE COMPONENTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Components", style = MaterialTheme.typography.displaySmall)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Extra building blocks for Material 3. $ready of ${entries.size} ready.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                itemsIndexed(entries, key = { _, entry -> entry.name }) { index, entry ->
                    ComponentCard(index = index + 1, entry = entry, onClick = { onOpen(entry) })
                }
            }
        }
    }
}

@Composable
private fun ComponentCard(index: Int, entry: ComponentEntry, onClick: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        enabled = entry.available,
        shape = CardShape,
        color = colors.surface,
        border = BorderStroke(1.dp, colors.outlineVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier
                .alpha(if (entry.available) 1f else 0.55f)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(
                        if (entry.available) colors.primaryContainer else colors.surfaceContainerHigh,
                        RoundedCornerShape(14.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = entry.icon,
                    contentDescription = null,
                    tint = if (entry.available) colors.onPrimaryContainer else colors.onSurfaceVariant,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(
                Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = index.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.size(8.dp))
                    Text(entry.title, style = MaterialTheme.typography.titleMedium)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurfaceVariant,
                )
            }
            if (entry.available) {
                Icon(AppIcons.ChevronRight, contentDescription = null, tint = colors.onSurfaceVariant)
            } else {
                Text(
                    text = "SOON",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier
                        .background(colors.surfaceContainerHigh, RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                )
            }
        }
    }
}

@Preview
@Composable
private fun CatalogPreview() {
    ComposeComponentsTheme(darkTheme = false) {
        CatalogScreen(isDark = false, onToggleTheme = {}, onOpen = {})
    }
}

@Preview
@Composable
private fun CatalogDarkPreview() {
    ComposeComponentsTheme(darkTheme = true) {
        CatalogScreen(isDark = true, onToggleTheme = {}, onOpen = {})
    }
}
