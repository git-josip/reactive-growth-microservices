package com.reactive.product.module.user.service

import com.reactive.product.common.jooq.DslContextTransactionAware
import com.reactive.product.module.user.domain.User
import com.reactive.product.module.user.domain.UserSignup
import com.reactive.product.module.user.mapper.toUser
import com.reactive.product.module.user.mapper.toUsersRecord
import com.reactive.product.module.user.repository.IUserJooqRepository
import com.reactive.product.module.user.validation.UserSignupValidator
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
): IUserService, DslContextTransactionAware {
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