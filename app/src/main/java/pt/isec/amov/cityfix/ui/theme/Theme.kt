package pt.isec.amov.cityfix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CityFixPetrolLight,
    onPrimary = CityFixPetrol,
    primaryContainer = CityFixPetrolDarkContainer,
    onPrimaryContainer = CityFixPetrolContainer,
    secondary = CityFixPetrolLight,
    onSecondary = CityFixPetrol,
    secondaryContainer = CityFixPetrolDarkContainer,
    onSecondaryContainer = CityFixPetrolContainer
)

private val LightColorScheme = lightColorScheme(
    primary = CityFixPetrol,
    onPrimary = Color.White,
    primaryContainer = CityFixPetrolContainer,
    onPrimaryContainer = CityFixPetrol,
    secondary = CityFixPetrolMuted,
    onSecondary = Color.White,
    secondaryContainer = CityFixPetrolContainer,
    onSecondaryContainer = CityFixPetrol
)

@Composable
fun CityFixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
