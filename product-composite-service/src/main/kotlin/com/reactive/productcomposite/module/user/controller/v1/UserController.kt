package com.reactive.productcomposite.module.user.controller.v1

import com.reactive.productcomposite.module.user.dto.request.UserSignupRequest
import com.reactive.productcomposite.module.user.dto.response.UserResponse
import com.reactive.productcomposite.module.user.mapper.toUserResponse
import com.reactive.productcomposite.module.user.mapper.toUserSignup
import com.reactive.productcomposite.module.user.service.IUserService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: IUserService
) {
    @PostMapping("/signup", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun signup(@RequestBody userSignupRequest: UserSignupRequest): UserResponse {
        return userService
            .signup(userSignupRequest.toUserSignup())
            .toUserResponse()
    }

    @GetMapping("/{userId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getById(@PathVariable userId: Long): UserResponse {
        return userService
            .getById(userId)
            .toUserResponse()
    }
}