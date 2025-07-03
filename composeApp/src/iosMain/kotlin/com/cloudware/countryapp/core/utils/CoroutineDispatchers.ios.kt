package com.cloudware.countryapp.core.utils

import kotlinx.coroutines.Dispatchers

/** iOS-specific implementation of coroutine dispatchers */
actual fun providePlatformDispatchers(): CoroutineDispatchers =
    CoroutineDispatchers(
        main = Dispatchers.Main,
        io = Dispatchers.Default, // iOS uses Default for IO operations
        default = Dispatchers.Default,
        unconfined = Dispatchers.Unconfined)
