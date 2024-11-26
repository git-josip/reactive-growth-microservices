package com.reactive.productcomposite.module.account.controller.v1

import com.reactive.productcomposite.common.pagination.mapper.toPaginationResponse
import com.reactive.productcomposite.common.pagination.response.PaginationResponse
import com.reactive.productcomposite.module.account.dto.request.AccountCreateRequest
import com.reactive.productcomposite.module.account.dto.response.AccountResponse
import com.reactive.productcomposite.module.account.mapper.toAccountCreate
import com.reactive.productcomposite.module.account.mapper.toAccountResponse
import com.reactive.productcomposite.module.account.service.IAccountService
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/accounts")
class AccountController(private val accountService: IAccountService) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createAccount(@RequestBody requestBody: AccountCreateRequest): AccountResponse {
        return accountService
            .createAccount(requestBody.toAccountCreate())
            .toAccountResponse()
    }

    @GetMapping("/{accountId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAccountBalance(@PathVariable accountId: String): AccountResponse {
        return accountService.getAccountById(accountId)
                .toAccountResponse()
    }

    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAllAccounts(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int
    ): PaginationResponse<AccountResponse> {
        return accountService.findAll(PageRequest.of(page, size))
            .map { it.toAccountResponse() }
            .toPaginationResponse()
    }
}