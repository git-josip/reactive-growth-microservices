package com.reactive.product.module.product.handler.http.v1

import com.reactive.product.common.pagination.mapper.toPaginationResponse
import com.reactive.product.common.pagination.response.PaginationResponse
import com.reactive.product.module.product.dto.request.ProductCreateRequest
import com.reactive.product.module.product.dto.response.ProductResponse
import com.reactive.product.module.product.mapper.toProductCreate
import com.reactive.product.module.product.mapper.toProductResponse
import com.reactive.product.module.product.service.IProductService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/products")
class ProductController(private val productService: IProductService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun create(@RequestBody requestBody: ProductCreateRequest): ProductResponse {
        return productService
            .create(requestBody.toProductCreate())
            .toProductResponse()
    }

    @GetMapping("/{id}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getById(@PathVariable id: Long): ProductResponse {
        return productService.getById(id)
                .toProductResponse()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAll(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int
    ): PaginationResponse<ProductResponse> {
        return productService.findAll(PageRequest.of(page, size))
            .map { it.toProductResponse() }
            .toPaginationResponse()
    }
}