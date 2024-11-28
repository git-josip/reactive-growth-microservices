package com.reactive.apigateway.common.decorator

import com.linecorp.armeria.server.HttpService
import com.linecorp.armeria.server.annotation.DecoratorFactory
import com.linecorp.armeria.server.annotation.DecoratorFactoryFunction
import com.linecorp.armeria.server.kotlin.CoroutineContextService
import kotlinx.coroutines.CoroutineName
import java.util.function.Function

@DecoratorFactory(CoroutineNameDecoratorFactory::class)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CoroutineNameDecorator(val name: String)
class CoroutineNameDecoratorFactory : DecoratorFactoryFunction<CoroutineNameDecorator> {
    override fun newDecorator(parameter: CoroutineNameDecorator): Function<in HttpService, out HttpService> {
        return CoroutineContextService.newDecorator {
            CoroutineName(parameter.name)
        }
    }
}