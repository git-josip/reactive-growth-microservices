package com.reactive.recommendation.common.events.domain

interface BaseMutexEvent {
    val mutexKey: String
}