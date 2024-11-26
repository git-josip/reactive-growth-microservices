package com.reactive.recommendation.module.transaction.controller

import com.reactive.recommendation.common.exception.dto.response.ErrorResponse
import com.reactive.recommendation.module.account.service.IAccountService
import com.reactive.recommendation.module.transaction.dto.request.TransactionCreateRequest
import com.reactive.recommendation.module.transaction.dto.response.TransactionResponse
import com.reactive.recommendation.module.transaction.mapper.toTransactionResponse
import com.reactive.recommendation.module.transaction.service.ITransactionService
import com.reactive.recommendation.utils.PropertyUtils
import com.reactive.recommendation.utils.SqlTestUtils
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters
import org.testcontainers.containers.PostgreSQLContainer
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.CONCURRENT)
class TransactionControllerTest(
    @Autowired private val accountService: IAccountService,
    @Autowired private val transactionService: ITransactionService,
    @Autowired private val connectionFactory: ConnectionFactory,
    @Autowired private val webClient: WebTestClient
) {
    @Nested
    inner class UserCreation {
        @ParameterizedTest
        @MethodSource("com.reactive.recommendation.module.transaction.controller.TransactionControllerTest#makeTransactionAccounts")
        fun `if validat request payload is provided transaction should be succesffully created and account balances updated`(
            fromAcc: String,
            toAcc: String,
            amount: BigDecimal
        ) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val fromAccBefore = accountService.getAccountById(fromAcc)
            val toAccBefore = accountService.getAccountById(toAcc)

            val transaction = webClient
                .post().uri("/api/v1/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    BodyInserters.fromValue(
                        TransactionCreateRequest(
                            fromAcc = fromAcc,
                            toAcc = toAcc,
                            amount = amount
                        )
                    )
                )
                .exchange()
                .expectStatus().isCreated
                .expectBody(TransactionResponse::class.java)
                .returnResult()
                .responseBody!!

            assertEquals(fromAcc, transaction.fromAcc)
            assertEquals(toAcc, transaction.toAcc)
            assertEquals(amount, transaction.amount)

            val fromAccAfter = accountService.getAccountById(fromAcc)
            val toAccAfter = accountService.getAccountById(toAcc)

            assertEquals(fromAccBefore.balance - amount,  fromAccAfter.balance)
            assertEquals(toAccBefore.balance + amount,  toAccAfter.balance)
        }
    }

    @Nested
    inner class AccountRetrieval {
        @Test
        fun `if provided not existing transaction id 404 NOT FOUND status should be returned`() {
            webClient
                .get().uri("/api/v1/transactions/${UUID.randomUUID()}")
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ErrorResponse::class.java)
                .isEqualTo(ErrorResponse(errorMsg = "Resource not found"))
                .returnResult()
        }

        @ParameterizedTest
        @MethodSource("com.reactive.recommendation.module.transaction.controller.TransactionControllerTest#existingTransactionIds")
        fun `if provided existing transaction id 200 OK status should be returned with valid data`(transactionId: UUID) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/transaction/create_transactions.sql"))

            val expectedTransaction = transactionService.getTransactionById(transactionId)

            webClient
                .get().uri("/api/v1/transactions/$transactionId")
                .exchange()
                .expectStatus().isOk
                .expectBody(TransactionResponse::class.java)
                .isEqualTo(expectedTransaction.toTransactionResponse())
        }
    }

    companion object {
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            PropertyUtils.configureCommonDynamicProperties(
                registry = registry,
                postgres = postgres
            )
        }


        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            postgres.start();
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            postgres.stop();
        }

        @JvmStatic
        fun makeTransactionAccounts() = listOf(
            Arguments.of("validacc1", "validacc2", BigDecimal("1000.00")),
            Arguments.of("validacc3", "validacc4", BigDecimal("20204.48"))
        )

        @JvmStatic
        fun existingTransactionIds() = listOf(
            Arguments.of("01930c90-dba6-7162-a689-ed96d038ef75"),
            Arguments.of("c7987aa1-f7bc-46e1-ad19-01d7c91eaa8d")
        )
    }
}