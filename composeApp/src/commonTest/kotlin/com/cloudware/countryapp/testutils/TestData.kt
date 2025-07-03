package com.cloudware.countryapp.testutils

import com.cloudware.countryapp.domain.model.Continent
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.model.Language

/** Test data factory for creating test instances of domain models */
object TestData {

  val testContinent = Continent(code = "NA", name = "North America")

  val testLanguage = Language(code = "en", name = "English")

  val testCountryUS =
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
          continent = testContinent,
          languages = listOf(testLanguage),
          awsRegion = "us-east-1")

  val testCountryCA =
      Country(
          code = "CA",
          name = "Canada",
          capital = "Ottawa",
          emoji = "🇨🇦",
          emojiU = "U+1F1E8 U+1F1E6",
          currency = "CAD",
          currencies = listOf("CAD"),
          phone = "1",
          phones = listOf("1"),
          native = "Canada",
          continent = testContinent,
          languages = listOf(testLanguage, Language("fr", "French")),
          awsRegion = "ca-central-1")

  val testCountryFR =
      Country(
          code = "FR",
          name = "France",
          capital = "Paris",
          emoji = "🇫🇷",
          emojiU = "U+1F1E5 U+1F1F7",
          currency = "EUR",
          currencies = listOf("EUR"),
          phone = "33",
          phones = listOf("33"),
          native = "France",
          continent = Continent("EU", "Europe"),
          languages = listOf(Language("fr", "French")),
          awsRegion = "eu-west-1")

  val testCountryGB =
      Country(
          code = "GB",
          name = "United Kingdom",
          capital = "London",
          emoji = "🇬🇧",
          emojiU = "U+1F1EC U+1F1E7",
          currency = "GBP",
          currencies = listOf("GBP"),
          phone = "44",
          phones = listOf("44"),
          native = "United Kingdom",
          continent = Continent("EU", "Europe"),
          languages = listOf(testLanguage),
          awsRegion = "eu-west-2")

  val testCountriesList = listOf(testCountryUS, testCountryCA, testCountryFR)
}
