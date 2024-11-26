package com.reactive.review.common.configuration

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi

object DispatchersConfiguration {
    @OptIn(ExperimentalCoroutinesApi::class)
    val DATABASE_DISPATCHERS = Dispatchers.IO.limitedParallelism(200)
}