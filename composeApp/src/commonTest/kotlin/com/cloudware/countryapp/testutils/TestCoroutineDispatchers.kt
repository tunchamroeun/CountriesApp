package com.cloudware.countryapp.testutils

import com.cloudware.countryapp.core.utils.CoroutineDispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

/**
 * Test implementation of CoroutineDispatchers that uses test dispatchers for predictable testing
 * behavior. Uses UnconfinedTestDispatcher for immediate execution in tests.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun testCoroutineDispatchers(testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) =
    CoroutineDispatchers(
        main = testDispatcher,
        io = testDispatcher,
        default = testDispatcher,
        unconfined = testDispatcher)
