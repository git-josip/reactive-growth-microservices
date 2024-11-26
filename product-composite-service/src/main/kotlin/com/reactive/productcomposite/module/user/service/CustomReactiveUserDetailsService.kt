package com.reactive.productcomposite.module.user.service

import kotlinx.coroutines.reactor.mono
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomReactiveUserDetailsService(private val userService: IUserService) : ReactiveUserDetailsService {
    override fun findByUsername(username: String): Mono<UserDetails> {
        return mono {
            val user = userService.tryGetByUsername(username)
            user?.let {
                User.withUsername(it.username)
                    .password(it.password)
                    .build()
            }
        }
    }
}