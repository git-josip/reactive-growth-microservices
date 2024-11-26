package com.reactive.review.module.user.mapper

import com.reactive.review.database.jooq.tables.records.UsersRecord
import com.reactive.review.module.user.domain.User
import com.reactive.review.module.user.domain.UserSignup
import com.reactive.review.module.user.dto.request.UserSignupRequest
import com.reactive.review.module.user.dto.response.UserResponse
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime
import java.time.ZoneId

fun  UsersRecord.toUser(): User {
    return User(
        id = this.id!!,
        username = this.username,
        password = this.password,
        createdAt = this.createdAt!!
    )
}

fun UserSignup.toUsersRecord(passwordEncoder: PasswordEncoder): UsersRecord {
    return UsersRecord(
        id = null,
        username = this.username,
        password = passwordEncoder.encode(this.password),
        createdAt = LocalDateTime.now(ZoneId.of("UTC"))
    )
}

fun User.toUserResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username
    )
}

fun UserSignupRequest.toUserSignup(): UserSignup {
    return UserSignup(
        username = this.username,
        password = this.password
    )
}