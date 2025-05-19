package com.example.drinkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.drinkit.ui.theme.DrinkItTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.isSystemInDarkTheme
import android.content.Context

class MainActivity : ComponentActivity() {
    companion object {
        private const val PREFS_NAME = "settings"
        private const val PREF_THEME_MODE = "theme_mode"
    }

    private fun saveThemeMode(context: Context, mode: ThemeMode) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(PREF_THEME_MODE, mode.name)
            .apply()
    }

    private fun loadThemeMode(context: Context): ThemeMode {
        val name = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(PREF_THEME_MODE, ThemeMode.SYSTEM.name)
        return try {
            ThemeMode.valueOf(name ?: ThemeMode.SYSTEM.name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var themeMode by remember { mutableStateOf(loadThemeMode(this)) }
            val isSystemDark = isSystemInDarkTheme()
            val darkTheme = when (themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemDark
            }

            var showSplashScreen by remember { mutableStateOf(true) }
            
            DrinkItTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                if (showSplashScreen) {
                    SplashScreen(onAnimationFinished = {
                        showSplashScreen = false
                    })
                } else {
                    AppNavigation(
                        themeMode = themeMode,
                        onThemeChange = {
                            themeMode = it
                            saveThemeMode(this, it)
                        }
                    )
                }
            }
        }
    }
}
