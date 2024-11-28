package com.reactive.apigateway.common.validation

abstract class Validator<T> {
    fun validate(item: T): com.reactive.apigateway.common.validation.ValidationResult<T> {
        val errors: MutableList<com.reactive.apigateway.common.validation.ValidationError> = mutableListOf()

        validationRules(item, errors)

        return com.reactive.apigateway.common.validation.ValidationResult(item, errors)
    }

    internal abstract fun validationRules(item: T, errors: MutableList<com.reactive.apigateway.common.validation.ValidationError>)
}
