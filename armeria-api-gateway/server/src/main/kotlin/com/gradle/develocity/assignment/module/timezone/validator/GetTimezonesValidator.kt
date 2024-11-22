package com.gradle.develocity.assignment.module.timezone.validator

import com.gradle.develocity.assignment.common.utils.ApplicationPropertiesUtils
import com.gradle.develocity.assignment.common.utils.convert
import com.gradle.develocity.assignment.common.validation.ValidationError
import com.gradle.develocity.assignment.common.validation.Validator
import com.gradle.develocity.assignment.module.timezone.dto.request.GetTimezones

class GetTimezonesValidator: Validator<GetTimezones>() {
    override fun validationRules(item: GetTimezones, errors: MutableList<ValidationError>) {
        if(item.city.isBlank()) {
            errors += ValidationError(
                field = FIELD_NAME,
                message = "Must not be empty"
            )
        }

        if(errors.isEmpty()) {
            val citiesParsed = item.city.split(
                TIMEZONE_DB_CITY_PARAM_SEPARATOR
            ).map { it.trim() }

            if (citiesParsed.size > TIMEZONE_DB_CITY_LIMIT) {
                errors += ValidationError(
                    field = FIELD_NAME,
                    message = "The number of cities exceeds the maximum allowed limit of $TIMEZONE_DB_CITY_LIMIT"
                )
            }

            citiesParsed.forEachIndexed { index, city ->
                val cityTrimmed = city.trim()

                if(cityTrimmed.isBlank()) {
                    errors += ValidationError(
                        field = "$FIELD_NAME[$index]",
                        message = "Must not be empty"
                    )
                } else if(cityTrimmed.startsWith("*") || cityTrimmed.endsWith("*")) {
                    val cityWithoutWildcardChar = cityTrimmed
                        .replace(WILD_CHAR, "")

                    if(cityWithoutWildcardChar.length < 5) {
                        errors += ValidationError(
                            field = "$FIELD_NAME[$index]",
                            message = "Minimum 5 characters needed for wildcard search."
                        )
                    }
                }
            }
        }
    }

    companion object {
        const val FIELD_NAME = "city"
        private val TIMEZONE_DB_CITY_PARAM_SEPARATOR = ApplicationPropertiesUtils.getProperty("timezone.db.city-param-separator")
        private val TIMEZONE_DB_CITY_LIMIT = ApplicationPropertiesUtils.getProperty("timezone.db.city.limit").convert<Int>()
        private const val WILD_CHAR = "*"
    }
}