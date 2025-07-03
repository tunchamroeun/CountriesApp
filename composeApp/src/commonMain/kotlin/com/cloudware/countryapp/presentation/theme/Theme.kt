package com.cloudware.countryapp.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/** Custom composition locals for additional theme properties */
val LocalSpacing = staticCompositionLocalOf { Spacing }

/** Custom shapes for the Countries app */
private val Shapes =
    Shapes(
        extraSmall = RoundedCornerShape(Spacing.Corner.Small),
        small = RoundedCornerShape(Spacing.Corner.Medium),
        medium = RoundedCornerShape(Spacing.Corner.Large),
        large = RoundedCornerShape(Spacing.Corner.ExtraLarge),
        extraLarge = RoundedCornerShape(Spacing.Large))

/**
 * Main theme composable for the Countries app
 *
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param content The content to be themed
 */
@Composable
fun CountriesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
  val colorScheme =
      if (darkTheme) {
        DarkColorScheme
      } else {
        LightColorScheme
      }

  CompositionLocalProvider(LocalSpacing provides Spacing) {
    MaterialTheme(
        colorScheme = colorScheme, typography = Typography, shapes = Shapes, content = content)
  }
}

/** Object to access theme properties */
object CountriesTheme {
  val spacing: Spacing
    @Composable get() = LocalSpacing.current

  val colors
    @Composable get() = MaterialTheme.colorScheme

  val typography
    @Composable get() = MaterialTheme.typography

  val shapes
    @Composable get() = MaterialTheme.shapes
}

/** Preview theme for development */
@Composable
fun CountriesThemePreview(darkTheme: Boolean = false, content: @Composable () -> Unit) {
  CountriesTheme(darkTheme = darkTheme, content = content)
}
