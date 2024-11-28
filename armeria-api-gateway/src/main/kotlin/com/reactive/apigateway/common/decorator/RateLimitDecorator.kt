package com.reactive.apigateway.common.decorator

import com.linecorp.armeria.common.HttpRequest
import com.linecorp.armeria.common.HttpResponse
import com.linecorp.armeria.common.HttpStatus
import com.linecorp.armeria.common.throttling.ThrottlingHeaders
import com.linecorp.armeria.server.AnnotatedServiceBindingBuilder
import com.linecorp.armeria.server.DecoratingHttpServiceFunction
import com.linecorp.armeria.server.HttpService
import com.linecorp.armeria.server.ServiceRequestContext
import com.linecorp.armeria.server.throttling.bucket4j.BandwidthLimit
import com.linecorp.armeria.server.throttling.bucket4j.TokenBucket
import com.linecorp.armeria.server.throttling.bucket4j.TokenBucketThrottlingStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.runBlocking
import java.time.Duration

class RateLimitDecorator(
    requestLimit: Long,
    limitDuration: Duration
) : DecoratingHttpServiceFunction {
    private val tokenBucket = TokenBucket
        .builder()
        .limits(BandwidthLimit.of(requestLimit, limitDuration))
        .build()

    private  val tokenBucketThrottlingStrategy = TokenBucketThrottlingStrategy.builder<HttpRequest>(tokenBucket)
        .name("rate-limit")
        .headersScheme(ThrottlingHeaders.RATELIMIT)
        .minimumBackoff(Duration.ofMinutes(1))
        .build()

    override fun serve(delegate: HttpService, ctx: ServiceRequestContext, req: HttpRequest): HttpResponse {
        return runBlocking(Dispatchers.IO) {
            val tryConsumeBucket = tokenBucketThrottlingStrategy.accept(ctx, req).asDeferred().await()
            if(!tryConsumeBucket) {
                HttpResponse.of(HttpStatus.TOO_MANY_REQUESTS)
            } else {
                delegate.serve(ctx, req)
            }
        }
    }

    companion object {
        fun AnnotatedServiceBindingBuilder.applyRateLimitDecorator(requestLimit: Long, limitDuration: Duration): AnnotatedServiceBindingBuilder {
            return this
                .decorator(RateLimitDecorator(
                    requestLimit = requestLimit,
                    limitDuration = limitDuration
                ))
        }
    }
}