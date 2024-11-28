package com.reactive.apigateway.common.utils

inline fun <reified T> String.convert(): T {
    return when(T::class){
        Int::class -> toInt()
        Long::class -> toLong()
        Double::class -> toDouble()
        Float::class -> toFloat()
        Boolean::class -> toBoolean()
        else -> error("Converter unavailable for ${T::class}")
    } as T
}