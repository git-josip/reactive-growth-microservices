package com.reactive.productcomposite.module.user.service

import com.reactive.productcomposite.common.exception.NotFoundException
import com.reactive.productcomposite.module.user.domain.User
import com.reactive.productcomposite.module.user.domain.UserSignup

interface IUserService {
    suspend fun tryGetById(id: Long): User?
    suspend fun tryGetByUsername(username: String): User?
    suspend fun signup(userSignup: UserSignup): User

    suspend fun getById(id: Long): User {
        return tryGetById(id) ?: throw NotFoundException("User with id '$id' does not exist")
    }
}