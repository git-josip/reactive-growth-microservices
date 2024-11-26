package com.reactive.product.module.account.controller

import com.reactive.product.common.exception.dto.response.ErrorResponse
import com.reactive.product.module.account.dto.request.AccountCreateRequest
import com.reactive.product.module.account.dto.response.AccountResponse
import com.reactive.product.module.account.mapper.toAccountResponse
import com.reactive.product.module.account.service.IAccountService
import com.reactive.product.utils.PropertyUtils
import com.reactive.product.utils.SqlTestUtils
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

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Execution(ExecutionMode.CONCURRENT)
class AccountControllerTest(
    @Autowired private val accountService: IAccountService,
    @Autowired private val connectionFactory: ConnectionFactory,
    @Autowired private val webClient: WebTestClient
) {
    @Nested
    inner class AccountCreation {
        @Test
        fun `if validat request payload is provided account should be succesffully created`() {
            webClient
                .post().uri("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                    BodyInserters.fromValue(
                        AccountCreateRequest(
                            id = "newvalidaccount",
                            balance = BigDecimal("1000.00")
                        )
                    )
                )
                .exchange()
                .expectStatus().isCreated
                .expectBody(AccountResponse::class.java)
                .isEqualTo(
                    AccountResponse(
                        id = "newvalidaccount",
                        balance = BigDecimal("1000.00")
                    )
                )
        }
    }

    @Nested
    inner class AccountRetrieval {
        @Test
        fun `if provided not existing account id 404 NOT FOUND status should be returned`() {
            webClient
                .get().uri("/api/v1/accounts/123invalid")
                .exchange()
                .expectStatus().isNotFound
                .expectBody(ErrorResponse::class.java)
                .isEqualTo(ErrorResponse(errorMsg = "Resource not found"))
        }

        @ParameterizedTest
        @MethodSource("com.reactive.product.module.account.controller.AccountControllerTest#accountSqlExistingIds")
        fun `if provided existing account id 200 OK status should be returned with valid data`(accountId: String) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val expectedAccount = accountService.getAccountById(accountId)

            webClient
                .get().uri("/api/v1/accounts/$accountId")
                .exchange()
                .expectStatus().isOk
                .expectBody(AccountResponse::class.java)
                .isEqualTo(expectedAccount.toAccountResponse())
        }
    }

    companion object {
        val postgres: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:16-alpine")

        @DynamicPropertySource
        @JvmStatic
        fun configureProperties(registry: DynamicPropertyRegistry) {
            PropertyUtils.configureCommonDynamicProperties(
                registry = registry,
                postgres = this.postgres
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
        fun accountSqlExistingIds() = listOf(
            Arguments.of("validacc1"),
            Arguments.of("validacc2"),
            Arguments.of("validacc3"),
            Arguments.of("validacc4"),
            Arguments.of("validacc5")
        )
    }
}