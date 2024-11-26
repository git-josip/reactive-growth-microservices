package com.reactive.order.common.events.domain

interface BaseMutexEvent {
    val mutexKey: String
}