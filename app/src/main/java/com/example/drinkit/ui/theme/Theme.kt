package com.example.drinkit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = SecondaryBlueDark,  // zaktualizowany secondary - turkusowy
    tertiary = BlueAccent80,
    onPrimary = Color.White,
    onSecondary = OnSecondaryDark,  // zaktualizowany na czarny dla wysokiego kontrastu
    background = DarkBackground,
    onBackground = DarkOnBackground
)

private val LightColorScheme = lightColorScheme(
    primary = Blue80,
    secondary = SecondaryBlue,     // zaktualizowany secondary - turkusowy
    tertiary = BlueAccent80,
    onPrimary = Color.White,
    onSecondary = OnSecondaryLight,  // zaktualizowany na czarny dla wysokiego kontrastu
    background = Color.White,
    onBackground = Navy
)

@Composable
fun DrinkItTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
