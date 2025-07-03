package com.cloudware.countryapp.core.utils

import kotlinx.coroutines.Dispatchers

/** Android-specific implementation of coroutine dispatchers */
actual fun providePlatformDispatchers(): CoroutineDispatchers =
    CoroutineDispatchers(
        main = Dispatchers.Main,
        io = Dispatchers.IO,
        default = Dispatchers.Default,
        unconfined = Dispatchers.Unconfined)
