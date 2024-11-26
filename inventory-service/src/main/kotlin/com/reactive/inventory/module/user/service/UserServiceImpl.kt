package com.reactive.inventory.module.user.service

import com.reactive.inventory.common.jooq.DslContextTransactionAware
import com.reactive.inventory.module.user.domain.User
import com.reactive.inventory.module.user.domain.UserSignup
import com.reactive.inventory.module.user.mapper.toUser
import com.reactive.inventory.module.user.mapper.toUsersRecord
import com.reactive.inventory.module.user.repository.IUserJooqRepository
import com.reactive.inventory.module.user.validation.UserSignupValidator
import org.jooq.Configuration
import org.jooq.DSLContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val transactionJooqRepository: IUserJooqRepository,
    private val userSignupValidator: UserSignupValidator,
    private val passwordEncoder: PasswordEncoder,
    override val dslContext: DSLContext
): com.reactive.inventory.module.user.service.IUserService, DslContextTransactionAware {
    override suspend fun tryGetById(id: Long): User? {
        return transactional { config: Configuration ->
            transactionJooqRepository
                .findById(id, config)
                ?.toUser()
        }
    }

    override suspend fun tryGetByUsername(username: String): User? {
        return transactional { config: Configuration ->
            transactionJooqRepository
                .findByUsername(username, config)
                ?.toUser()
        }
    }

    override suspend fun signup(userSignup: UserSignup): User {
        return transactional { config: Configuration ->
            userSignupValidator.validate(userSignup, config).failOnError()

            transactionJooqRepository.insert(userSignup.toUsersRecord(passwordEncoder), config)
                .toUser()
        }
    }
}