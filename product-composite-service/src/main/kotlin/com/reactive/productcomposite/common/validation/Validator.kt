package com.reactive.productcomposite.common.validation

import org.jooq.Configuration

interface Validator<T> {
    suspend fun validate(item: T, config: Configuration): com.reactive.productcomposite.common.validation.ValidationResult<T> {
        val errors: MutableList<ValidationError> = mutableListOf()

        validationRules(item, config, errors)

        return com.reactive.productcomposite.common.validation.ValidationResult(item, errors)
    }

    suspend fun validationRules(item: T, config: Configuration, errors: MutableList<ValidationError>)
}
