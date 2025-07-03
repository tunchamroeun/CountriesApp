package com.cloudware.countryapp.domain.model

/**
 * Domain model representing a language
 *
 * @param code The unique language code (e.g., "en", "es", "fr")
 * @param name The human-readable name of the language
 * @param native The native name of the language (in the language itself)
 * @param rtl Whether the language is read from right-to-left
 */
data class Language(
    val code: String,
    val name: String,
    val native: String? = null,
    val rtl: Boolean = false
)
