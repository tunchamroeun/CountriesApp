package com.cloudware.countryapp

interface Platform {
  val name: String
  val isAndroid: Boolean
  val isIOS: Boolean

  /** Gets the device's current locale/language code (e.g., "en", "es", "fr") */
  fun getDeviceLocale(): String

  /** Checks if the device has network connectivity */
  fun isNetworkAvailable(): Boolean

  /** Shares text content using the platform's native sharing mechanism */
  fun shareText(text: String, subject: String? = null)

  /** Opens a phone dialer with the provided phone number */
  fun dialPhoneNumber(phoneNumber: String)

  /** Opens the platform's default maps application to show a country/location */
  fun openMaps(countryName: String)

  /** Opens a URL in the platform's default web browser */
  fun openUrl(url: String)

  /** Gets platform-specific app version or build information */
  fun getAppVersion(): String
}

expect fun getPlatform(): Platform
