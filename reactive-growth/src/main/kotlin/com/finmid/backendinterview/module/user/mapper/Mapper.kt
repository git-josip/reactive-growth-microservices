package com.finmid.backendinterview.module.user.mapper

import com.finmid.backendinterview.database.jooq.tables.records.UsersRecord
import com.finmid.backendinterview.module.user.domain.User
import com.finmid.backendinterview.module.user.domain.UserSignup
import com.finmid.backendinterview.module.user.dto.request.UserSignupRequest
import com.finmid.backendinterview.module.user.dto.response.UserResponse
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