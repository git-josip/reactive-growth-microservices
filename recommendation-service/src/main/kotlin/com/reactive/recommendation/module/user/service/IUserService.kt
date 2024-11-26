package com.reactive.recommendation.module.user.service

import com.reactive.recommendation.common.exception.NotFoundException
import com.reactive.recommendation.module.user.domain.User
import com.reactive.recommendation.module.user.domain.UserSignup

interface IUserService {
    suspend fun tryGetById(id: Long): User?
    suspend fun tryGetByUsername(username: String): User?
    suspend fun signup(userSignup: UserSignup): User

    suspend fun getById(id: Long): User {
        return tryGetById(id) ?: throw NotFoundException("User with id '$id' does not exist")
    }
}