package com.cloudware.countryapp.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography =
    Typography(
        // Display styles - Large headings
        displayLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp),
        displayMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 45.sp,
                lineHeight = 52.sp,
                letterSpacing = 0.sp),
        displaySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                lineHeight = 44.sp,
                letterSpacing = 0.sp),

        // Headline styles - Section headings
        headlineLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                letterSpacing = 0.sp),
        headlineMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                lineHeight = 36.sp,
                letterSpacing = 0.sp),
        headlineSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 32.sp,
                letterSpacing = 0.sp),

        // Title styles - Card titles, dialog titles
        titleLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp),
        titleMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp),
        titleSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp),

        // Body styles - Main content text
        bodyLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp),
        bodyMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp),
        bodySmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp),

        // Label styles - Buttons, tabs, chips
        labelLarge =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp),
        labelMedium =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp),
        labelSmall =
            TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp))

// Custom text styles for specific use cases in the Countries app
object CountryAppTextStyles {
  val CountryName =
      Typography.titleLarge.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp)

  val CountryCode = Typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp)

  val Population = Typography.bodyMedium.copy(fontWeight = FontWeight.Normal)

  val Capital = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium)

  val Language = Typography.labelMedium.copy(fontWeight = FontWeight.Normal)

  val Continent = Typography.labelSmall.copy(fontWeight = FontWeight.Medium, letterSpacing = 0.8.sp)

  val SearchPlaceholder = Typography.bodyLarge.copy(fontWeight = FontWeight.Normal)

  val ErrorMessage = Typography.bodyMedium.copy(fontWeight = FontWeight.Medium)

  val LoadingText = Typography.bodyMedium.copy(fontWeight = FontWeight.Normal)
}
