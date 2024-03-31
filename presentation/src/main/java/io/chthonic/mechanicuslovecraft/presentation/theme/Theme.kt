package io.chthonic.mechanicuslovecraft.presentation.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColors(
    surface = DraculaDarkestPurple, // top app bar
    primary = DraculaPurple,
    primaryVariant = DraculaDarkestPurple,
    secondary = DraculaYellow,
    secondaryVariant = DraculaPink,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onSurface = Color.White,
    background = DraculaBlack,
)
private val LightColors = lightColors(
    primary = DraculaPurple, // top app bar
    primaryVariant = DraculaDarkestPurple,
    secondary = DraculaYellow,
    secondaryVariant = DraculaPink,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    background = DraculaBlack,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AppTheme(
    isDarkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (isDarkTheme) DarkColors else LightColors,
        content = content
    )
}