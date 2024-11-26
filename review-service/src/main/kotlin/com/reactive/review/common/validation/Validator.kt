package com.reactive.review.common.validation

import org.jooq.Configuration

interface Validator<T> {
    suspend fun validate(item: T, config: Configuration): com.reactive.review.common.validation.ValidationResult<T> {
        val errors: MutableList<ValidationError> = mutableListOf()

        validationRules(item, config, errors)

        return com.reactive.review.common.validation.ValidationResult(item, errors)
    }

    suspend fun validationRules(item: T, config: Configuration, errors: MutableList<ValidationError>)
}
