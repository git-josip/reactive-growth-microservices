package com.finmid.backendinterview.module.user.validation

import com.finmid.backendinterview.common.validation.ValidationError
import com.finmid.backendinterview.common.validation.Validator
import com.finmid.backendinterview.module.user.domain.UserSignup
import com.finmid.backendinterview.module.user.repository.IUserJooqRepository
import org.jooq.Configuration
import org.springframework.stereotype.Component

@Component
class UserSignupValidator(private val userJooqRepository: IUserJooqRepository): Validator<UserSignup> {
    override suspend fun validationRules(item: UserSignup, config: Configuration, errors: MutableList<ValidationError>) {
        val maybeUser = userJooqRepository.findByUsername(item.username, config)

        maybeUser?.let {
            errors.add(
                ValidationError(
                    field = UserSignup::username.name,
                    message = "User with provided username already exists."
                )
            )
        }
    }
}