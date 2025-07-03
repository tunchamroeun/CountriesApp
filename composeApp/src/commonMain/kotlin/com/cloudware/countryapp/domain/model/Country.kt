package com.cloudware.countryapp.domain.model

/**
 * Domain model representing a country with complete information
 *
 * This model contains all the information that can be retrieved from the GraphQL API and represents
 * the core country entity in our domain.
 */
data class Country(
    val code: String,
    val name: String,
    val capital: String?,
    val emoji: String,
    val emojiU: String? = null,
    val currency: String?,
    val currencies: List<String> = emptyList(),
    val phone: String? = null,
    val phones: List<String> = emptyList(),
    val native: String? = null,
    val continent: Continent,
    val languages: List<Language> = emptyList(),
    val awsRegion: String? = null
) {
  /** Returns the display name for the country, typically combining emoji and name */
  fun getDisplayName(): String = "$emoji $name"

  /** Returns the primary currency or the first currency from the list */
  fun getPrimaryCurrency(): String? = currency ?: currencies.firstOrNull()

  /** Returns the primary phone code or the first phone code from the list */
  fun getPrimaryPhone(): String? = phone ?: phones.firstOrNull()

  /** Returns whether the country has a capital city defined */
  fun hasCapital(): Boolean = !capital.isNullOrBlank()

  /** Returns the number of official languages */
  fun getLanguageCount(): Int = languages.size

  /** Returns whether the country is in a specific continent */
  fun isInContinent(continentCode: String): Boolean =
      continent.code.equals(continentCode, ignoreCase = true)
}
