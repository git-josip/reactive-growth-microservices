package com.reactive.productcomposite.module.account.service

import com.reactive.productcomposite.common.jooq.DslContextTransactionAware
import com.reactive.productcomposite.module.account.domain.Account
import com.reactive.productcomposite.module.account.domain.AccountCreate
import com.reactive.productcomposite.module.account.mapper.toAccount
import com.reactive.productcomposite.module.account.mapper.toAccountsRecord
import com.reactive.productcomposite.module.account.repository.IAccountJooqRepository
import com.reactive.productcomposite.module.account.validation.AccountCreateValidator
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class AccountServiceImpl(
    private val accountRepository: IAccountJooqRepository,
    private val accountCreateValidator: AccountCreateValidator,
    override val dslContext: DSLContext
) : IAccountService, DslContextTransactionAware {
    override suspend fun createAccount(accountCreate: AccountCreate): Account {
        return transactional { config: Configuration ->
            accountCreateValidator.validate(accountCreate, config).failOnError()

            accountRepository
                .insert(accountCreate.toAccountsRecord(), config)
                .toAccount()
        }
    }

    override suspend fun tryGetAccountById(id: String): Account? {
        return transactional { config: Configuration ->
            accountRepository
                .findById(id, config)
                ?.toAccount()
        }
    }

    override suspend fun count(): Int {
        return transactional { config: Configuration ->
            accountRepository
                .count(config)
        }
    }

    override suspend fun findAll(pageable: Pageable): Page<Account> {
        return transactional { config: Configuration ->
            accountRepository
                .findAll(pageable, config)
                .map { it.toAccount() }
        }
    }
}