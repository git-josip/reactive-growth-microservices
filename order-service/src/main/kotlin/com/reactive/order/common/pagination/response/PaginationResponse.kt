package com.reactive.order.common.pagination.response

data class PaginationResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val total: Long
)
