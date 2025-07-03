package com.cloudware.countryapp.presentation.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Design system spacing constants for consistent UI layout */
object Spacing {

  // Base spacing units
  val None: Dp = 0.dp
  val ExtraSmall: Dp = 4.dp
  val Small: Dp = 8.dp
  val Medium: Dp = 16.dp
  val Large: Dp = 24.dp
  val ExtraLarge: Dp = 32.dp
  val Huge: Dp = 48.dp
  val Massive: Dp = 64.dp

  // Component-specific spacing
  object Card {
    val Padding = Medium
    val Margin = Small
    val InnerSpacing = Small
    val VerticalSpacing = ExtraSmall
  }

  object Screen {
    val Horizontal = Medium
    val Vertical = Medium
    val TopPadding = Large
    val BottomPadding = Medium
  }

  object List {
    val ItemSpacing = Small
    val SectionSpacing = Large
    val ItemPadding = Medium
  }

  object Button {
    val Padding = Medium
    val Margin = Small
    val InnerSpacing = Small
    val IconSpacing = ExtraSmall
  }

  object SearchBar {
    val Padding = Medium
    val Margin = Small
    val IconSpacing = Small
    val HorizontalPadding = Medium
    val VerticalPadding = Small
  }

  object CountryCard {
    val Padding = Medium
    val Margin = Small
    val FlagSpacing = Medium
    val ContentSpacing = ExtraSmall
    val TitleSpacing = ExtraSmall
  }

  object Details {
    val SectionSpacing = Large
    val ItemSpacing = Medium
    val LabelSpacing = ExtraSmall
    val ContentPadding = Medium
  }

  object Navigation {
    val BarHeight = 56.dp
    val IconSpacing = Medium
    val TitleSpacing = Small
  }

  object Divider {
    val Thickness = 1.dp
    val Spacing = Medium
  }

  object Icon {
    val Small = 16.dp
    val Medium = 24.dp
    val Large = 32.dp
    val ExtraLarge = 48.dp
  }

  object Flag {
    val Small = 24.dp
    val Medium = 32.dp
    val Large = 48.dp
    val ExtraLarge = 64.dp
  }

  object Corner {
    val Small = 4.dp
    val Medium = 8.dp
    val Large = 12.dp
    val ExtraLarge = 16.dp
  }

  object Elevation {
    val Small = 2.dp
    val Medium = 4.dp
    val Large = 8.dp
    val ExtraLarge = 16.dp
  }
}

/** Extension properties for common spacing combinations */
object SpacingCombinations {
  val CardSpacing = Spacing.Card.Padding
  val ScreenPadding = Spacing.Screen.Horizontal
  val ListItemSpacing = Spacing.List.ItemSpacing
  val ButtonSpacing = Spacing.Button.Padding
  val SectionSpacing = Spacing.Details.SectionSpacing
}
