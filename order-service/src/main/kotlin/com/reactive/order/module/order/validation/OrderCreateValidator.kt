package com.reactive.order.module.order.validation

import com.reactive.order.common.validation.ValidationError
import com.reactive.order.common.validation.Validator
import com.reactive.order.module.order.domain.OrderCreate
import org.jooq.Configuration
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class OrderCreateValidator: Validator<OrderCreate> {
    override suspend fun validationRules(item: OrderCreate, config: Configuration, errors: MutableList<ValidationError>) {
        if(item.price <= BigDecimal.ZERO) {
            errors.add(
                ValidationError(
                    field = OrderCreate::price.name,
                    message = "Price must be positive."
                )
            )
        }

        if (item.quantity <= 0) {
            errors.add(
                ValidationError(
                    field = OrderCreate::quantity.name,
                    message = "Quantity must be positive."
                )
            )
        }
    }
}