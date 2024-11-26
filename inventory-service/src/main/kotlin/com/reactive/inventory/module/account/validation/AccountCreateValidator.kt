package com.reactive.inventory.module.account.validation

import com.reactive.inventory.common.validation.ValidationError
import com.reactive.inventory.common.validation.Validator
import com.reactive.inventory.module.account.domain.AccountCreate
import com.reactive.inventory.module.account.repository.IAccountJooqRepository
import org.jooq.Configuration
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class AccountCreateValidator(private val accountJooqRepository: IAccountJooqRepository): Validator<AccountCreate> {
    override suspend fun validationRules(item: AccountCreate, config: Configuration, errors: MutableList<ValidationError>) {
        val maybeAccount = accountJooqRepository.findById(item.id, config)
        if(maybeAccount != null) {
            errors.add(
                ValidationError(
                    field = AccountCreate::id.name,
                    message = "Account with provided id already exist."
                )
            )
        }

        if(item.id.length > 40) {
            errors.add(
                ValidationError(
                    field = AccountCreate::id.name,
                    message = "The account ID must not exceed 40 characters."
                )
            )
        }

        if(!ACCOUNT_FORMAT_NUMBERS_AND_LOWER_CASE.matches(item.id)) {
            errors.add(
                ValidationError(
                    field = AccountCreate::id.name,
                    message = "The account ID can only contain lowercase letters, numbers, hyphens (-), and underscores (_)."
                )
            )
        }

        if(item.balance <= BigDecimal.ZERO) {
            errors.add(
                ValidationError(
                    field = AccountCreate::balance.name,
                    message = "Balance must be positive."
                )
            )
        }
    }

    companion object {
        val ACCOUNT_FORMAT_NUMBERS_AND_LOWER_CASE = "^[a-z0-9_-]+\$".toRegex()
    }
}