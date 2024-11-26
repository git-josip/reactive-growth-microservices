package com.reactive.product.common.events.domain

interface BaseMutexEvent {
    val mutexKey: String
}