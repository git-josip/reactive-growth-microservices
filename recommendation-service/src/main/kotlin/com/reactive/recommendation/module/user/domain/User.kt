package com.reactive.recommendation.module.user.domain

import java.time.LocalDateTime

data class User(
    val id: Long,
    val username: String,
    val password: String,
    val createdAt: LocalDateTime
)
