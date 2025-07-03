package com.cloudware.countryapp.core.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Wrapper class for coroutine dispatchers to provide consistent access and enable easier testing
 * through dependency injection
 *
 * @param main Main dispatcher for UI operations (Android Main/iOS Main thread)
 * @param io IO dispatcher for I/O operations like network calls and file operations
 * @param default Default dispatcher for CPU-intensive operations
 * @param unconfined Unconfined dispatcher for testing and special use cases
 */
class CoroutineDispatchers(
    val main: CoroutineDispatcher = Dispatchers.Main,
    val io: CoroutineDispatcher = Dispatchers.Default, // Use platform-specific implementation
    val default: CoroutineDispatcher = Dispatchers.Default,
    val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
)

/**
 * Platform-specific dispatcher provider Can be overridden in platform-specific modules for custom
 * behavior
 */
expect fun providePlatformDispatchers(): CoroutineDispatchers
