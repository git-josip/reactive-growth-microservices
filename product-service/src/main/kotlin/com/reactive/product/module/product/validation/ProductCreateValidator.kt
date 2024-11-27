package com.reactive.product.module.product.validation

import com.reactive.product.common.validation.ValidationError
import com.reactive.product.common.validation.Validator
import com.reactive.product.module.product.domain.ProductCreate
import com.reactive.product.module.product.repository.IProductJooqRepository
import org.jooq.Configuration
import org.springframework.stereotype.Component

@Component
class ProductCreateValidator(private val productJooqRepository: IProductJooqRepository): Validator<ProductCreate> {
    override suspend fun validationRules(item: ProductCreate, config: Configuration, errors: MutableList<ValidationError>) {
        val maybeProduct = productJooqRepository.findByName(item.name, config)
        if(maybeProduct != null) {
            errors.add(
                ValidationError(
                    field = ProductCreate::name.name,
                    message = "Product with provided name already exist."
                )
            )
        }

        if(item.name.length > 255) {
            errors.add(
                ValidationError(
                    field = ProductCreate::name.name,
                    message = "The product name must not exceed 255 characters."
                )
            )
        }
    }
}