package com.finmid.backendinterview.module.transaction.controller.v1

import com.finmid.backendinterview.module.transaction.dto.request.TransactionCreateRequest
import com.finmid.backendinterview.module.transaction.dto.response.TransactionResponse
import com.finmid.backendinterview.module.transaction.mapper.toTransactionCreate
import com.finmid.backendinterview.module.transaction.mapper.toTransactionResponse
import com.finmid.backendinterview.module.transaction.service.ITransactionService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionController(
    private val transactionService: ITransactionService
) {
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.CREATED)
    suspend fun createTransaction(@RequestBody transaction: TransactionCreateRequest) {
        return transactionService
            .publishTransaction(transaction.toTransactionCreate())
    }

    @GetMapping("/{transactionId}")
    suspend fun getTransactionById(@PathVariable transactionId: UUID): TransactionResponse {
        return transactionService
            .getTransactionById(transactionId)
            .toTransactionResponse()
    }
}