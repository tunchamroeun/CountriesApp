package com.cloudware.countryapp.core.di

import org.kodein.di.DI
import org.kodein.di.DirectDI
import org.kodein.di.direct

/**
 * Dependency injection container for the application Provides centralized access to all
 * dependencies
 */
object DIContainer {

  /** Main DI container instance - initialized by platform-specific code */
  lateinit var di: DI
    private set

  /** Direct DI instance for direct access when needed */
  val directDI: DirectDI
    get() = di.direct

  /**
   * Initializes the DI container with the provided instance Should be called early in the app
   * lifecycle by platform-specific code
   */
  fun initialize(diInstance: DI) {
    di = diInstance
  }

  /** Checks if the DI container has been initialized */
  fun isInitialized(): Boolean = ::di.isInitialized
}
