package com.cloudware.countryapp.domain.model

/**
 * Domain model representing a continent
 *
 * @param code The unique continent code (e.g., "AS", "EU", "NA")
 * @param name The human-readable name of the continent
 */
data class Continent(val code: String, val name: String)
