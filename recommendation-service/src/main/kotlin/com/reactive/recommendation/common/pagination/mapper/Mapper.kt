package com.reactive.recommendation.common.pagination.mapper

import com.reactive.recommendation.common.pagination.response.PaginationResponse
import org.springframework.data.domain.Page

fun <T> Page<T>.toPaginationResponse(): PaginationResponse<T> {
    return PaginationResponse(
        content = this.content,
        page = this.number,
        size = this.size,
        total = this.totalElements
    )
}