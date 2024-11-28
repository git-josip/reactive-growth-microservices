package com.reactive.apigateway.module.product.service.grpc.external

import com.reactive.apigateway.module.product.dto.request.CreateProductRequest
import com.reactive.apigateway.module.product.mapper.toDomainProductResponse
import com.reactive.apigateway.module.product.mapper.toGrpcCreateProductRequest
import com.linecorp.armeria.client.grpc.GrpcClients
import com.linecorp.armeria.client.limit.ConcurrencyLimitingClient
import com.linecorp.armeria.client.retry.Backoff
import com.linecorp.armeria.client.retry.RetryRule
import com.linecorp.armeria.client.retry.RetryingClient
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.server.annotation.*
import com.reactive.apigateway.grpc.product.GetByIdRequest
import com.reactive.apigateway.grpc.product.ProductServiceGrpcKt
import kotlinx.coroutines.*

@ExceptionHandler(com.reactive.apigateway.common.exception.handler.ValidationExceptionHandler::class)
class ProductService {
    val retryRule = RetryRule.onStatus(HttpStatus.SERVICE_UNAVAILABLE)
        .orElse(RetryRule.onStatus(HttpStatus.INTERNAL_SERVER_ERROR))
        .orElse(RetryRule.onException(io.grpc.StatusException::class.java))
        .orElse(RetryRule.onException(io.grpc.StatusRuntimeException::class.java))

    val backoff = Backoff.exponential(1000, 16000).withMaxAttempts(2)


    val productGrpcServiceClient: ProductServiceGrpcKt.ProductServiceCoroutineStub = GrpcClients.builder("gproto+http://127.0.0.1:7071/")
        .decorator(RetryingClient.newDecorator(retryRule))
        .decorator(ConcurrencyLimitingClient.newDecorator(200))
        .build(ProductServiceGrpcKt.ProductServiceCoroutineStub::class.java)

    @Get("/{id}")
    @ProducesJson
    @Description("Retrieve product by id")
    suspend fun getProductById(@Param("id") id: Long): com.reactive.apigateway.module.product.dto.response.ProductResponse {
        return withContext(productDispatcher) {
            productGrpcServiceClient.getProductById(
                GetByIdRequest.newBuilder().setId(id).build()
            ).toDomainProductResponse()
        }
    }

    @Post
    @ProducesJson
    @Description("Create product")
    suspend fun createProduct(request: CreateProductRequest): com.reactive.apigateway.module.product.dto.response.ProductResponse {
        return withContext(productDispatcher) {
            productGrpcServiceClient.createProduct(
                request.toGrpcCreateProductRequest()
            ).toDomainProductResponse()
        }
    }

    companion object {
        val productDispatcher = Dispatchers.IO.limitedParallelism(200, "productDispatcher")
    }
}
