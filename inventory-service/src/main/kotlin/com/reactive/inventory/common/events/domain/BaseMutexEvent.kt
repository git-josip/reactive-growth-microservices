package com.reactive.inventory.common.events.domain

interface BaseMutexEvent {
    val mutexKey: String
}