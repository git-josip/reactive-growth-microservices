package com.reactive.inventory.module.user.validation

import com.reactive.inventory.common.validation.ValidationError
import com.reactive.inventory.common.validation.Validator
import com.reactive.inventory.module.user.domain.UserSignup
import com.reactive.inventory.module.user.repository.IUserJooqRepository
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