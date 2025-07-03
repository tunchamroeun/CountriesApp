package com.cloudware.countryapp.presentation.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Primary Colors - Blue theme inspired by flags and global nature
val Primary = Color(0xFF1976D2)
val PrimaryVariant = Color(0xFF1565C0)
val OnPrimary = Color(0xFFFFFFFF)

val Secondary = Color(0xFF2E7D8A)
val SecondaryVariant = Color(0xFF26696F)
val OnSecondary = Color(0xFFFFFFFF)

// Background Colors
val Background = Color(0xFFFAFAFA)
val OnBackground = Color(0xFF1C1B1F)

val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF1C1B1F)

val SurfaceVariant = Color(0xFFF3F1F5)
val OnSurfaceVariant = Color(0xFF49454F)

// Error Colors
val Error = Color(0xFFBA1A1A)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)

// Additional Custom Colors
val Success = Color(0xFF2E7D32)
val Warning = Color(0xFFED6C02)
val Info = Color(0xFF0288D1)

// Dark Theme Colors
val DarkPrimary = Color(0xFF90CAF9)
val DarkPrimaryVariant = Color(0xFF64B5F6)
val DarkOnPrimary = Color(0xFF003258)

val DarkSecondary = Color(0xFF4DD0E1)
val DarkSecondaryVariant = Color(0xFF26C6DA)
val DarkOnSecondary = Color(0xFF003640)

val DarkBackground = Color(0xFF121212)
val DarkOnBackground = Color(0xFFE6E1E5)

val DarkSurface = Color(0xFF1E1E1E)
val DarkOnSurface = Color(0xFFE6E1E5)

val DarkSurfaceVariant = Color(0xFF49454F)
val DarkOnSurfaceVariant = Color(0xFFCAC4D0)

// Country-specific colors for flags and regions
val FlagRed = Color(0xFFD32F2F)
val FlagBlue = Color(0xFF1976D2)
val FlagGreen = Color(0xFF388E3C)
val FlagYellow = Color(0xFFFBC02D)

val LightColorScheme =
    lightColorScheme(
        primary = Primary,
        onPrimary = OnPrimary,
        primaryContainer = PrimaryVariant,
        onPrimaryContainer = OnPrimary,
        secondary = Secondary,
        onSecondary = OnSecondary,
        secondaryContainer = SecondaryVariant,
        onSecondaryContainer = OnSecondary,
        tertiary = Info,
        onTertiary = OnPrimary,
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,
        background = Background,
        onBackground = OnBackground,
        surface = Surface,
        onSurface = OnSurface,
        surfaceVariant = SurfaceVariant,
        onSurfaceVariant = OnSurfaceVariant,
        outline = OnSurfaceVariant,
        outlineVariant = SurfaceVariant)

val DarkColorScheme =
    darkColorScheme(
        primary = DarkPrimary,
        onPrimary = DarkOnPrimary,
        primaryContainer = DarkPrimaryVariant,
        onPrimaryContainer = DarkOnPrimary,
        secondary = DarkSecondary,
        onSecondary = DarkOnSecondary,
        secondaryContainer = DarkSecondaryVariant,
        onSecondaryContainer = DarkOnSecondary,
        tertiary = Info,
        onTertiary = DarkOnPrimary,
        error = Error,
        onError = OnError,
        errorContainer = ErrorContainer,
        onErrorContainer = OnErrorContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOnSurfaceVariant,
        outlineVariant = DarkSurfaceVariant)
