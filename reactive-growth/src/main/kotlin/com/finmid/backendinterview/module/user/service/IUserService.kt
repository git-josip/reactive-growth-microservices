package com.finmid.backendinterview.module.user.service

import com.finmid.backendinterview.common.exception.NotFoundException
import com.finmid.backendinterview.module.user.domain.User
import com.finmid.backendinterview.module.user.domain.UserSignup

interface IUserService {
    suspend fun tryGetById(id: Long): User?
    suspend fun tryGetByUsername(username: String): User?
    suspend fun signup(userSignup: UserSignup): User

    suspend fun getById(id: Long): User {
        return tryGetById(id) ?: throw NotFoundException("User with id '$id' does not exist")
    }
}