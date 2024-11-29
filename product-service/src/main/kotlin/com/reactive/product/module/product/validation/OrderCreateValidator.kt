package com.reactive.product.module.product.validation

import com.reactive.product.common.validation.ValidationError
import com.reactive.product.common.validation.Validator
import com.reactive.product.module.product.domain.OrderCreate
import com.reactive.product.module.product.domain.ProductCreate
import com.reactive.product.module.product.mapper.toProduct
import com.reactive.product.module.product.repository.IProductJooqRepository
import org.jooq.Configuration
import org.springframework.stereotype.Component

@Component
class OrderCreateValidator(private val productJooqRepository: IProductJooqRepository): Validator<OrderCreate> {
    override suspend fun validationRules(item: OrderCreate, config: Configuration, errors: MutableList<ValidationError>) {
        val maybeProduct = productJooqRepository.findById(item.productId, config)
        if(maybeProduct == null) {
            errors.add(
                ValidationError(
                    field = OrderCreate::productId.name,
                    message = "Product with provided id does not exist."
                )
            )
        } else {
            if (maybeProduct.toProduct().price != item.price) {
                errors.add(
                    ValidationError(
                        field = OrderCreate::price.name,
                        message = "The product price does not match the product price in the database."
                    )
                )
            }
        }

        if(item.quantity < 0) {
            errors.add(
                ValidationError(
                    field = ProductCreate::quantity.name,
                    message = "The product quantity must be greater than or equal to 0."
                )
            )
        }
    }
}