package com.reactive.review.module.account.service

import com.reactive.review.common.exception.NotFoundException
import com.reactive.review.module.account.domain.Account
import com.reactive.review.module.account.domain.AccountCreate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IAccountService {
    suspend fun createAccount(accountCreate: AccountCreate): Account
    suspend fun tryGetAccountById(id: String): Account?
    suspend fun count(): Int
    suspend fun findAll(pageable: Pageable): Page<Account>

    suspend fun getAccountById(id: String): Account {
        return tryGetAccountById(id) ?: throw NotFoundException("Account with id '$id' does not exist")
    }
}