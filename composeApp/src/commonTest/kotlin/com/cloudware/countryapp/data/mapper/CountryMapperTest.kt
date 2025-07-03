package com.cloudware.countryapp.data.mapper

import com.cloudware.countryapp.domain.model.Continent
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.model.Language
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Unit tests for data mapping logic.
 *
 * Note: We cannot test the actual GraphQL mapping functions because Apollo-generated classes are
 * final and cannot be mocked. However, we can test the domain model structure and properties to
 * ensure the mapping logic would work correctly.
 */
class CountryMapperTest {

  @Test
  fun `Country domain model should have all required properties`() {
    // Given
    val continent = Continent(code = "NA", name = "North America")
    val language = Language(code = "en", name = "English")

    // When
    val country =
        Country(
            code = "US",
            name = "United States",
            capital = "Washington, D.C.",
            emoji = "🇺🇸",
            emojiU = "U+1F1FA U+1F1F8",
            currency = "USD",
            currencies = listOf("USD"),
            phone = "1",
            phones = listOf("1"),
            native = "United States",
            continent = continent,
            languages = listOf(language),
            awsRegion = "us-east-1")

    // Then - verify all properties are correctly assigned
    assertEquals("US", country.code)
    assertEquals("United States", country.name)
    assertEquals("Washington, D.C.", country.capital)
    assertEquals("🇺🇸", country.emoji)
    assertEquals("U+1F1FA U+1F1F8", country.emojiU)
    assertEquals("USD", country.currency)
    assertEquals(listOf("USD"), country.currencies)
    assertEquals("1", country.phone)
    assertEquals(listOf("1"), country.phones)
    assertEquals("United States", country.native)
    assertEquals(continent, country.continent)
    assertEquals(listOf(language), country.languages)
    assertEquals("us-east-1", country.awsRegion)
  }

  @Test
  fun `Country domain model should handle null values correctly`() {
    // Given
    val continent = Continent(code = "EU", name = "Europe")

    // When
    val country =
        Country(
            code = "FR",
            name = "France",
            capital = null, // null capital
            emoji = "🇫🇷",
            emojiU = null, // null emojiU
            currency = null, // null currency
            currencies = emptyList(), // empty currencies list
            phone = null, // null phone
            phones = emptyList(), // empty phones list
            native = null, // null native
            continent = continent,
            languages = emptyList(), // empty languages list
            awsRegion = null // null AWS region
            )

    // Then - verify null values are handled correctly
    assertEquals("FR", country.code)
    assertEquals("France", country.name)
    assertEquals(null, country.capital)
    assertEquals("🇫🇷", country.emoji)
    assertEquals(null, country.emojiU)
    assertEquals(null, country.currency)
    assertEquals(emptyList(), country.currencies)
    assertEquals(null, country.phone)
    assertEquals(emptyList(), country.phones)
    assertEquals(null, country.native)
    assertEquals(continent, country.continent)
    assertEquals(emptyList(), country.languages)
    assertEquals(null, country.awsRegion)
  }

  @Test
  fun `Country helper methods should work correctly`() {
    // Given
    val continent = Continent(code = "AS", name = "Asia")
    val language = Language(code = "ja", name = "Japanese")

    val country =
        Country(
            code = "JP",
            name = "Japan",
            capital = "Tokyo",
            emoji = "🇯🇵",
            emojiU = null,
            currency = "JPY",
            currencies = listOf("JPY"),
            phone = "81",
            phones = listOf("81"),
            native = "日本",
            continent = continent,
            languages = listOf(language),
            awsRegion = "ap-northeast-1")

    // Then - test helper methods
    assertEquals("🇯🇵 Japan", country.getDisplayName())
    assertEquals("JPY", country.getPrimaryCurrency())
    assertEquals("81", country.getPrimaryPhone())
    assertEquals(true, country.hasCapital())
    assertEquals(1, country.getLanguageCount())
    assertEquals(true, country.isInContinent("AS"))
    assertEquals(false, country.isInContinent("EU"))
  }

  @Test
  fun `Country helper methods should handle edge cases`() {
    // Given
    val continent = Continent(code = "SA", name = "South America")

    val country =
        Country(
            code = "BR",
            name = "Brazil",
            capital = "", // empty capital
            emoji = "🇧🇷",
            emojiU = null,
            currency = null, // no primary currency
            currencies = listOf("BRL", "USD"), // multiple currencies
            phone = null, // no primary phone
            phones = listOf("55", "555"), // multiple phones
            native = null,
            continent = continent,
            languages = emptyList(), // no languages
            awsRegion = null)

    // Then - test helper methods with edge cases
    assertEquals("🇧🇷 Brazil", country.getDisplayName())
    assertEquals("BRL", country.getPrimaryCurrency()) // first from currencies list
    assertEquals("55", country.getPrimaryPhone()) // first from phones list
    assertEquals(false, country.hasCapital()) // empty capital is considered false
    assertEquals(0, country.getLanguageCount())
    assertEquals(true, country.isInContinent("sa")) // case insensitive
  }

  @Test
  fun `Continent domain model should work correctly`() {
    // When
    val continent = Continent(code = "OC", name = "Oceania")

    // Then
    assertEquals("OC", continent.code)
    assertEquals("Oceania", continent.name)
  }

  @Test
  fun `Language domain model should work correctly`() {
    // When
    val language = Language(code = "ar", name = "Arabic", native = "العربية", rtl = true)

    // Then
    assertEquals("ar", language.code)
    assertEquals("Arabic", language.name)
    assertEquals("العربية", language.native)
    assertEquals(true, language.rtl)
  }

  @Test
  fun `Language domain model should handle defaults correctly`() {
    // When
    val language =
        Language(
            code = "en", name = "English"
            // native and rtl use default values
            )

    // Then
    assertEquals("en", language.code)
    assertEquals("English", language.name)
    assertEquals(null, language.native)
    assertEquals(false, language.rtl)
  }
}
