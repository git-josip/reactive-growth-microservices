package com.reactive.recommendation.module.account.service

import com.reactive.recommendation.common.configuration.JooqConfiguration
import com.reactive.recommendation.common.configuration.R2dbcConfiguration
import com.reactive.recommendation.common.exception.NotFoundException
import com.reactive.recommendation.common.exception.ValidationException
import com.reactive.recommendation.common.validation.ValidationError
import com.reactive.recommendation.module.account.domain.AccountCreate
import com.reactive.recommendation.utils.PropertyUtils
import com.reactive.recommendation.utils.SqlTestUtils
import io.r2dbc.spi.ConnectionFactory
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


@Testcontainers
@SpringBootTest
@Import(JooqConfiguration::class, R2dbcConfiguration::class)
@Execution(ExecutionMode.CONCURRENT)
class AccountServiceTest(
    @Autowired private val accountService: IAccountService,
    @Autowired private val connectionFactory: ConnectionFactory
) {
    @Nested
    inner class AccountCreation {
        @ParameterizedTest
        @MethodSource("com.reactive.recommendation.module.account.service.AccountServiceTest#accountInvalidIds")
        fun `should fail if invalid id is provided`(invalidId: String) = runTest {
            val exception = assertFailsWith<ValidationException>(
                block = {
                    accountService.createAccount(
                        AccountCreate(
                            id= invalidId,
                            balance = BigDecimal.TEN,
                            version = 1
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = AccountCreate::id.name,
                        message = "The account ID can only contain lowercase letters, numbers, hyphens (-), and underscores (_)."
                    )
                ),
                exception.errors
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.recommendation.module.account.service.AccountServiceTest#accountValidIdsAndInvalidBalance")
        fun `should fail if zero or negative balance is provided`(id: String, balance: BigDecimal) = runTest {
            val exception = assertFailsWith<ValidationException>(
                block = {
                    accountService.createAccount(
                        AccountCreate(
                            id= id,
                            balance = balance,
                            version = 0
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = AccountCreate::balance.name,
                        message = "Balance must be positive."
                    )
                ),
                exception.errors
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.recommendation.module.account.service.AccountServiceTest#accountValidIdsAndValidBalance")
        fun `should succeed valid id and balance are provided`(id: String, balance: BigDecimal) = runTest {
            val account = accountService.createAccount(
                AccountCreate(
                    id= id,
                    balance = balance,
                    version = 0
                )
            )

            assertEquals(id, account.id)
            assertEquals(balance, account.balance)
            assertEquals(0, account.version)
        }
    }

    @Nested
    inner class AccountRetrieval {
        @ParameterizedTest
        @MethodSource("com.reactive.recommendation.module.account.service.AccountServiceTest#accountNonExistingIds")
        fun `should fail if non existing account id is provided`(id: String) = runTest {
            val exception = assertFailsWith<NotFoundException>(
                block = {
                    accountService.getAccountById(id)
                }
            )

            assertEquals(
                "Account with id '$id' does not exist",
                exception.message
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.recommendation.module.account.service.AccountServiceTest#accountSqlExistingIds")
        fun `should succeed if existing account id is provided`(id: String) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val account = accountService.getAccountById(id)

            assertEquals(id, account.id)
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
        fun accountInvalidIds() = listOf(
            Arguments.of(""),
            Arguments.of(" "),
            Arguments.of("123 rre"),
            Arguments.of("123RRR"),
            Arguments.of("wqweeq$$"),
        )

        @JvmStatic
        fun accountNonExistingIds() = listOf(
            Arguments.of("notexist1"),
            Arguments.of("notexist2")
        )

        @JvmStatic
        fun accountValidIdsAndInvalidBalance() = listOf(
            Arguments.of("test1", BigDecimal.ZERO),
            Arguments.of("test2", BigDecimal(-10)),
            Arguments.of("test3", BigDecimal(-100)),
        )

        @JvmStatic
        fun accountValidIdsAndValidBalance() = listOf(
            Arguments.of("validtest1", BigDecimal("100.00")),
            Arguments.of("validtest2", BigDecimal("1000.00")),
            Arguments.of("validtest3", BigDecimal("10000.00")),
        )

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