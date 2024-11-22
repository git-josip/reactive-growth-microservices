package com.finmid.backendinterview.common.validation

import org.jooq.Configuration

interface Validator<T> {
    suspend fun validate(item: T, config: Configuration): ValidationResult<T> {
        val errors: MutableList<ValidationError> = mutableListOf()

        validationRules(item, config, errors)

        return ValidationResult(item, errors)
    }

    suspend fun validationRules(item: T, config: Configuration, errors: MutableList<ValidationError>)
}
