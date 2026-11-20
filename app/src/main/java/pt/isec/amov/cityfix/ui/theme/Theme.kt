package pt.isec.amov.cityfix.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private const val LARGE_TEXT_SCALE = 1.2f

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
    largeText: Boolean = false,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val textDensity = if (largeText) Density(density.density, density.fontScale * LARGE_TEXT_SCALE) else density
    CompositionLocalProvider(LocalDensity provides textDensity) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            content = content
        )
    }
}
