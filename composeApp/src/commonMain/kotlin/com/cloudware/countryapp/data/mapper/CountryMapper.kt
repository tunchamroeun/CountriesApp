package com.cloudware.countryapp.data.mapper

import com.cloudware.countryapp.CountriesQuery
import com.cloudware.countryapp.CountryQuery
import com.cloudware.countryapp.domain.model.Continent
import com.cloudware.countryapp.domain.model.Country
import com.cloudware.countryapp.domain.model.Language

/**
 * Mapper functions to convert GraphQL generated models to domain models.
 *
 * These mappers handle the transformation between the data layer (GraphQL models) and the domain
 * layer (domain models), including null safety and default values.
 */

/**
 * Maps a GraphQL CountriesQuery.Country to a domain Country model.
 *
 * @return Domain Country model with available data from the GraphQL response
 */
fun CountriesQuery.Country.toDomainModel(): Country {
  return Country(
      code = this.code,
      name = this.name,
      capital = this.capital,
      emoji = this.emoji,
      // Set defaults for fields not available in CountriesQuery
      emojiU = null,
      currency = null,
      currencies = emptyList(),
      phone = null,
      phones = emptyList(),
      native = null,
      continent = this.continent.toDomainModel(),
      languages = this.languages.map { it.toDomainModel() },
      awsRegion = null)
}

/**
 * Maps a GraphQL CountriesQuery.Continent to a domain Continent model.
 *
 * @return Domain Continent model
 */
fun CountriesQuery.Continent.toDomainModel() = Continent(code = this.code, name = this.name)

/**
 * Maps a GraphQL CountriesQuery.Language to a domain Language model.
 *
 * @return Domain Language model
 */
fun CountriesQuery.Language.toDomainModel() = Language(code = this.code, name = this.name)

/**
 * Maps a GraphQL CountryQuery.Country to a domain Country model.
 *
 * @return Domain Country model with detailed information from the GraphQL response
 */
fun CountryQuery.Country.toDomainModel(): Country {
  return Country(
      code = this.code,
      name = this.name,
      capital = this.capital,
      emoji = this.emoji,
      emojiU = null,
      currency = this.currency,
      currencies = emptyList(),
      phone = this.phone,
      phones = this.phones,
      native = null,
      continent = this.continent.toDomainModel(),
      languages = this.languages.map { it.toDomainModel() },
      awsRegion = null // Not queried in current schema
      )
}

/**
 * Maps a GraphQL CountryQuery.Continent to a domain Continent model.
 *
 * @return Domain Continent model
 */
fun CountryQuery.Continent.toDomainModel(): Continent {
  return Continent(code = this.code, name = this.name)
}

/**
 * Maps a GraphQL CountryQuery.Language to a domain Language model.
 *
 * @return Domain Language model
 */
fun CountryQuery.Language.toDomainModel(): Language {
  return Language(code = this.code, name = this.name, native = this.native, rtl = false)
}
