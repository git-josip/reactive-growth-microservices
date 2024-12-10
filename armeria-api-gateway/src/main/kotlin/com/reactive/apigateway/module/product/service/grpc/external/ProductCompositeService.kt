package com.reactive.apigateway.module.product.service.grpc.external

import com.reactive.apigateway.module.product.mapper.toDomainProductResponse
import com.linecorp.armeria.client.grpc.GrpcClients
import com.linecorp.armeria.client.limit.ConcurrencyLimitingClient
import com.linecorp.armeria.client.retry.RetryRule
import com.linecorp.armeria.client.retry.RetryingClient
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.server.annotation.*
import com.reactive.apigateway.common.exception.handler.GrpcExceptionHandler
import com.reactive.apigateway.grpc.order.GetAllByProductIdRequest
import com.reactive.apigateway.grpc.order.OrderServiceGrpcKt
import com.reactive.apigateway.grpc.product.GetByIdRequest
import com.reactive.apigateway.grpc.product.ProductServiceGrpcKt
import com.reactive.apigateway.module.product.dto.response.CompositeProductResponse
import com.reactive.apigateway.module.product.mapper.toDomainOrderResponse
import kotlinx.coroutines.*

@ExceptionHandler(GrpcExceptionHandler::class)
class ProductCompositeService {
    val retryRule = RetryRule.onStatus(HttpStatus.SERVICE_UNAVAILABLE)
        .orElse(RetryRule.onStatus(HttpStatus.INTERNAL_SERVER_ERROR))
        .orElse(RetryRule.onException(io.grpc.StatusException::class.java))
        .orElse(RetryRule.onException(io.grpc.StatusRuntimeException::class.java))

    val productGrpcServiceClient: ProductServiceGrpcKt.ProductServiceCoroutineStub = GrpcClients.builder("gproto+http://$productServiceGrpcHost:$productServiceGrpcPort/")
        .decorator(RetryingClient.newDecorator(retryRule))
        .decorator(ConcurrencyLimitingClient.newDecorator(200))
        .build(ProductServiceGrpcKt.ProductServiceCoroutineStub::class.java)

    val orderGrpcServiceClient: OrderServiceGrpcKt.OrderServiceCoroutineStub = GrpcClients.builder("gproto+http://$orderServiceGrpcHost:$orderServiceGrpcPort/")
        .decorator(RetryingClient.newDecorator(retryRule))
        .decorator(ConcurrencyLimitingClient.newDecorator(200))
        .build(OrderServiceGrpcKt.OrderServiceCoroutineStub::class.java)

    @Get("/{id}")
    @ProducesJson
    @Description("Retrieve product composite by id")
    suspend fun getProductById(@Param("id") id: Long): com.reactive.apigateway.module.product.dto.response.CompositeProductResponse {
        return withContext(productDispatcher) {
            val productById = async {  productGrpcServiceClient.getProductById(
                    GetByIdRequest.newBuilder().setId(id).build()
                )
            }

            val productOrders = async {  orderGrpcServiceClient.getAllByProductId(
                    GetAllByProductIdRequest.newBuilder().setProductId(id).build()
                )
            }

            CompositeProductResponse(
                product = productById.await().toDomainProductResponse(),
                orders = productOrders.await().ordersList.map { it.toDomainOrderResponse() }
            )
        }
    }

    companion object {
        val productDispatcher = Dispatchers.IO.limitedParallelism(200, "productDispatcher")


        val productServiceGrpcHost = System.getenv("PRODUCT_SERVICE_GRPC_HOST") ?: "localhost"
        val productServiceGrpcPort = System.getenv("PRODUCT_SERVICE_GRPC_PORT") ?: "7071"

        val orderServiceGrpcHost = System.getenv("ORDER_SERVICE_GRPC_HOST") ?: "localhost"
        val orderServiceGrpcPort = System.getenv("ORDER_SERVICE_GRPC_PORT") ?: "7073"

        val inventoryServiceGrpcHost = System.getenv("INVENTORY_SERVICE_GRPC_HOST") ?: "localhost"
        val inventoryServiceGrpcPort = System.getenv("INVENTORY_SERVICE_GRPC_PORT") ?: "7072"
    }
}
