package com.nameisjayant.composecomponents

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.nameisjayant.composecomponents.catalog.CatalogScreen
import com.nameisjayant.composecomponents.catalog.ComponentEntry
import com.nameisjayant.composecomponents.datepicker.DatePickerDemoScreen
import com.nameisjayant.composecomponents.ui.theme.ComposeComponentsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // null = follow the system; the toggle pins light or dark.
            var darkOverride by rememberSaveable { mutableStateOf<Boolean?>(null) }
            val isDark = darkOverride ?: isSystemInDarkTheme()

            // Keep status/navigation bar icons readable when the in-app toggle disagrees with the system.
            DisposableEffect(isDark) {
                val style = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { isDark }
                enableEdgeToEdge(statusBarStyle = style, navigationBarStyle = style)
                onDispose {}
            }

            ComposeComponentsTheme(darkTheme = isDark) {
                ComponentsApp(isDark = isDark, onToggleTheme = { darkOverride = !isDark })
            }
        }
    }
}

@Composable
private fun ComponentsApp(isDark: Boolean, onToggleTheme: () -> Unit) {
    var open by rememberSaveable { mutableStateOf<ComponentEntry?>(null) }
    BackHandler(enabled = open != null) { open = null }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AnimatedContent(
            targetState = open,
            transitionSpec = {
                val forward = targetState != null
                val slideIn = slideInHorizontally(tween(320)) { if (forward) it / 4 else -it / 4 }
                val slideOut = slideOutHorizontally(tween(320)) { if (forward) -it / 4 else it / 4 }
                (slideIn + fadeIn(tween(320))) togetherWith (slideOut + fadeOut(tween(200)))
            },
            label = "Screen",
        ) { entry ->
            when (entry) {
                null -> CatalogScreen(isDark = isDark, onToggleTheme = onToggleTheme, onOpen = { open = it })
                else -> ComponentDemo(entry, isDark, onToggleTheme, onBack = { open = null })
            }
        }
    }
}

@Composable
private fun ComponentDemo(entry: ComponentEntry, isDark: Boolean, onToggleTheme: () -> Unit, onBack: () -> Unit) {
    when (entry) {
        ComponentEntry.DatePicker -> DatePickerDemoScreen(isDark, onToggleTheme, onBack)
        // Not built yet; the catalog doesn't open these.
        else -> CatalogScreen(isDark = isDark, onToggleTheme = onToggleTheme, onOpen = {})
    }
}
