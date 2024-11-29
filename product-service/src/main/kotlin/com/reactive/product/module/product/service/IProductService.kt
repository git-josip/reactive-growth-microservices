package com.reactive.product.module.product.service

import com.reactive.product.common.exception.NotFoundException
import com.reactive.product.module.product.domain.OrderCreate
import com.reactive.product.module.product.domain.Product
import com.reactive.product.module.product.domain.ProductCreate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface IProductService {
    suspend fun create(productCreate: ProductCreate): Product
    suspend fun createOrder(orderCreate: OrderCreate)
    suspend fun tryGetById(id: Long): Product?
    suspend fun count(): Int
    suspend fun findAll(pageable: Pageable): Page<Product>

    suspend fun getById(id: Long): Product {
        return tryGetById(id) ?: throw NotFoundException("Product with id '$id' does not exist")
    }
}