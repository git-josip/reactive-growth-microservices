package com.reactive.product.module.transaction.service

import com.reactive.product.common.configuration.JooqConfiguration
import com.reactive.product.common.configuration.R2dbcConfiguration
import com.reactive.product.common.exception.NotFoundException
import com.reactive.product.common.exception.ValidationException
import com.reactive.product.common.validation.ValidationError
import com.reactive.product.module.account.service.IAccountService
import com.reactive.product.module.transaction.domain.TransactionCreate
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
import org.springframework.context.annotation.Import
import org.springframework.core.io.ClassPathResource
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.TimeZone
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith


@Testcontainers
@SpringBootTest
@Import(JooqConfiguration::class, R2dbcConfiguration::class)
@Execution(ExecutionMode.CONCURRENT)
class TransactionServiceTest(
    @Autowired private val transactionService: ITransactionService,
    @Autowired private val accountService: IAccountService,
    @Autowired private val connectionFactory: ConnectionFactory
) {
    @Nested
    inner class UserCreation {
        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#accountInvalidFormattedIds")
        fun `should fail if invalid formatted fromAcc is provided`(fromAcc: String) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val exception = assertFailsWith<ValidationException>(
                block = {
                    transactionService.createTransaction(
                        TransactionCreate(
                            fromAcc = fromAcc,
                            toAcc = "validacc1",
                            amount = BigDecimal(1000),
                            createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = TransactionCreate::fromAcc.name,
                        message = "The account ID can only contain lowercase letters, numbers, hyphens (-), and underscores (_)."
                    )
                ),
                exception.errors
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#accountInvalidFormattedIds")
        fun `should fail if invalid formatted toAcc is provided`(toAcc: String) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val exception = assertFailsWith<ValidationException>(
                block = {
                    transactionService.createTransaction(
                        TransactionCreate(
                            fromAcc = "validacc1",
                            toAcc = toAcc,
                            amount = BigDecimal(1000),
                            createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = TransactionCreate::toAcc.name,
                        message = "The account ID can only contain lowercase letters, numbers, hyphens (-), and underscores (_)."
                    )
                ),
                exception.errors
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#accountNotExistingIds")
        fun `should fail if not existing fromAcc is provided`(fromAcc: String) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val exception = assertFailsWith<ValidationException>(
                block = {
                    transactionService.createTransaction(
                        TransactionCreate(
                            fromAcc = fromAcc,
                            toAcc = "validacc1",
                            amount = BigDecimal(1000),
                            createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = TransactionCreate::fromAcc.name,
                        message = "Account with provided id does not exist exist."
                    )
                ),
                exception.errors
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#accountNotExistingIds")
        fun `should fail if not existing toAcc is provided`(toAcc: String) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val exception = assertFailsWith<ValidationException>(
                block = {
                    transactionService.createTransaction(
                        TransactionCreate(
                            fromAcc = "validacc1",
                            toAcc = toAcc,
                            amount = BigDecimal(1000),
                            createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = TransactionCreate::toAcc.name,
                        message = "Account with provided id does not exist exist."
                    )
                ),
                exception.errors
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#invalidAmounts")
        fun `should fail if invalid amount is provided`(amount: BigDecimal) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val exception = assertFailsWith<ValidationException>(
                block = {
                    transactionService.createTransaction(
                        TransactionCreate(
                            fromAcc = "validacc1",
                            toAcc = "validacc2",
                            amount = amount,
                            createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = TransactionCreate::amount.name,
                        message = "Transaction amount must be positive."
                    )
                ),
                exception.errors
            )
        }

        @Test
        fun `should fail if same source and destination accounts are provided`() = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val exception = assertFailsWith<ValidationException>(
                block = {
                    transactionService.createTransaction(
                        TransactionCreate(
                            fromAcc = "validacc1",
                            toAcc = "validacc1",
                            amount = BigDecimal(100),
                            createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = TransactionCreate::fromAcc.name,
                        message = "The source account cannot be the same as the destination account. Please select different accounts for the transaction."
                    )
                ),
                exception.errors
            )
        }

        @Test
        fun `should fail if amount exceeds fromAcc balance`() = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val exception = assertFailsWith<ValidationException>(
                block = {
                    transactionService.createTransaction(
                        TransactionCreate(
                            fromAcc = "validacc1",
                            toAcc = "validacc2",
                            amount = BigDecimal(1_000_000),
                            createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                        )
                    )
                }
            )

            assertEquals(
                listOf(
                    ValidationError(
                        field = TransactionCreate::amount.name,
                        message = "The transaction amount exceeds the balance of the source account [100000.00]. Please check the balance and try again."
                    )
                ),
                exception.errors
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#accountTransactionValidIdsAndValidAmount")
        fun `should succeed if valid TransactionCreate is provided`(fromAcc: String, toAcc: String, amount: BigDecimal) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))

            val fromAccBefore = accountService.getAccountById(fromAcc)
            val toAccBefore = accountService.getAccountById(toAcc)

            val transaction = transactionService.createTransaction(
                TransactionCreate(
                    fromAcc = fromAcc,
                    toAcc = toAcc,
                    amount = amount,
                    createdAt = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId())
                )
            )

            val fromAccAfter = accountService.getAccountById(fromAcc)
            val toAccAfter = accountService.getAccountById(toAcc)

            assertEquals(amount,  transaction.amount)
            assertEquals(fromAcc,  transaction.fromAcc)
            assertEquals(toAcc,  transaction.toAcc)
            assertEquals(fromAccBefore.balance - amount,  fromAccAfter.balance)
            assertEquals(toAccBefore.balance + amount,  toAccAfter.balance)
        }
    }

    @Nested
    inner class UserRetrieval {
        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#transactionNotExistingIds")
        fun `should fail if non existing transaction id is provided`(id: UUID) = runTest {
            val exception = assertFailsWith<NotFoundException>(
                block = {
                    transactionService.getTransactionById(id)
                }
            )

            assertEquals(
                "Transaction with id '$id' does not exist",
                exception.message
            )
        }

        @ParameterizedTest
        @MethodSource("com.reactive.product.module.transaction.service.TransactionServiceTest#transactionExistingIds")
        fun `should succeed if existing account id is provided`(id: UUID) = runTest {
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/account/create_accounts.sql"))
            SqlTestUtils.executeTestSqlScriptBlocking(connectionFactory, ClassPathResource("/sql/transaction/create_transactions.sql"))

            val transaction = transactionService.getTransactionById(id)

            assertEquals(id, transaction.id)
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
        fun accountInvalidFormattedIds() = listOf(
            Arguments.of(""),
            Arguments.of(" "),
            Arguments.of("123 rre"),
            Arguments.of("123RRR"),
            Arguments.of("wqweeq$$"),
        )

        @JvmStatic
        fun accountNotExistingIds() = listOf(
            Arguments.of("notexist1"),
            Arguments.of("notexist2")
        )

        @JvmStatic
        fun invalidAmounts() = listOf(
            Arguments.of(BigDecimal.ZERO),
            Arguments.of(BigDecimal(-10)),
            Arguments.of(BigDecimal(-100)),
        )

        @JvmStatic
        fun accountTransactionValidIdsAndValidAmount() = listOf(
            Arguments.of("validacc1", "validacc2", BigDecimal("8888.33")),
            Arguments.of("validacc3", "validacc4", BigDecimal("50000.00"))
        )

        @JvmStatic
        fun transactionNotExistingIds() = listOf(
            Arguments.of(UUID.fromString("949c2bc5-e3c1-45ae-9e35-d4e6c5a47bd4")),
            Arguments.of(UUID.fromString("7512599d-886a-42f9-869e-3a9dc610212f"))
        )

        @JvmStatic
        fun transactionExistingIds() = listOf(
            Arguments.of(UUID.fromString("01930c90-dba6-7162-a689-ed96d038ef75")),
            Arguments.of(UUID.fromString("c7987aa1-f7bc-46e1-ad19-01d7c91eaa8d"))
        )
    }
}