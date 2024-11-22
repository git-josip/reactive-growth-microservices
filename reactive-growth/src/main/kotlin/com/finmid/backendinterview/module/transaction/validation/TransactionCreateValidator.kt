package com.finmid.backendinterview.module.transaction.validation

import com.finmid.backendinterview.common.validation.ValidationError
import com.finmid.backendinterview.common.validation.Validator
import com.finmid.backendinterview.module.account.repository.IAccountJooqRepository
import com.finmid.backendinterview.module.account.validation.AccountCreateValidator.Companion.ACCOUNT_FORMAT_NUMBERS_AND_LOWER_CASE
import com.finmid.backendinterview.module.transaction.domain.TransactionCreate
import org.jooq.Configuration
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class TransactionCreateValidator(private val accountJooqRepository: IAccountJooqRepository): Validator<TransactionCreate> {
    override suspend fun validationRules(item: TransactionCreate, config: Configuration, errors: MutableList<ValidationError>) {
        if(!ACCOUNT_FORMAT_NUMBERS_AND_LOWER_CASE.matches(item.fromAcc)) {
            errors.add(
                ValidationError(
                    field = TransactionCreate::fromAcc.name,
                    message = "The account ID can only contain lowercase letters, numbers, hyphens (-), and underscores (_)."
                )
            )
        } else {
            val maybeFromAccount = accountJooqRepository.findById(item.fromAcc, config)
            if(maybeFromAccount == null) {
                errors.add(
                    ValidationError(
                        field = TransactionCreate::fromAcc.name,
                        message = "Account with provided id does not exist exist."
                    )
                )
            } else {
                if(maybeFromAccount.balance < item.amount) {
                    errors.add(
                        ValidationError(
                            field = TransactionCreate::amount.name,
                            message = "The transaction amount exceeds the balance of the source account [${maybeFromAccount.balance}]. Please check the balance and try again."
                        )
                    )
                }
            }
        }

        if(!ACCOUNT_FORMAT_NUMBERS_AND_LOWER_CASE.matches(item.toAcc)) {
            errors.add(
                ValidationError(
                    field = TransactionCreate::toAcc.name,
                    message = "The account ID can only contain lowercase letters, numbers, hyphens (-), and underscores (_)."
                )
            )
        } else {
            val maybeToAccount = accountJooqRepository.findById(item.toAcc, config)
            if(maybeToAccount == null) {
                errors.add(
                    ValidationError(
                        field = TransactionCreate::toAcc.name,
                        message = "Account with provided id does not exist exist."
                    )
                )
            }

            if(item.fromAcc == item.toAcc) {
                errors.add(
                    ValidationError(
                        field = TransactionCreate::fromAcc.name,
                        message = "The source account cannot be the same as the destination account. Please select different accounts for the transaction."
                    )
                )
            }
        }

        if(item.amount <= BigDecimal.ZERO) {
            errors.add(
                ValidationError(
                    field = TransactionCreate::amount.name,
                    message = "Transaction amount must be positive."
                )
            )
        } else {
            if(item.amount.scale() > 2) {
                errors.add(
                    ValidationError(
                        field = TransactionCreate::amount.name,
                        message = "Maximum number of decimal places allowed is 2."
                    )
                )
            }
        }
    }
}