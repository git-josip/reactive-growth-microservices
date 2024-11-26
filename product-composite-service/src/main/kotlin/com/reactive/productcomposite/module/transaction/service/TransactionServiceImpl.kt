package com.reactive.productcomposite.module.transaction.service

import com.reactive.productcomposite.common.configuration.ObjectMapperConfiguration
import com.reactive.productcomposite.common.exception.AccountStateChangedException
import com.reactive.productcomposite.common.jooq.DslContextTransactionAware
import com.reactive.productcomposite.module.account.repository.IAccountJooqRepository
import com.reactive.productcomposite.module.transaction.domain.Transaction
import com.reactive.productcomposite.module.transaction.domain.TransactionCreate
import com.reactive.productcomposite.module.transaction.event.domain.TransactionCreatedEvent
import com.reactive.productcomposite.common.events.publisher.IEventPubService
import com.reactive.productcomposite.module.transaction.event.kafka.KafkaProducerService
import com.reactive.productcomposite.module.transaction.mapper.toTransaction
import com.reactive.productcomposite.module.transaction.mapper.toTransactionCreatedEvent
import com.reactive.productcomposite.module.transaction.mapper.toTransactionsRecord
import com.reactive.productcomposite.module.transaction.repository.ITransactionJooqRepository
import com.reactive.productcomposite.module.transaction.validation.TransactionCreateValidator
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.kotlin.coroutines.transactionCoroutine
import org.springframework.stereotype.Service
import java.util.*

@Service
class TransactionServiceImpl(
    private val transactionJooqRepository: ITransactionJooqRepository,
    private val accountJooqRepository: IAccountJooqRepository,
    private val transactionCreateValidator: TransactionCreateValidator,
    private val eventPublisherService: IEventPubService<TransactionCreatedEvent>,
    private val kafkaProducerService: KafkaProducerService,
    override val dslContext: DSLContext
): com.reactive.productcomposite.module.transaction.service.ITransactionService, DslContextTransactionAware {
    override suspend fun tryGetTransactionById(id: UUID): Transaction? {
        return dslContext.transactionCoroutine { config: Configuration ->
            transactionJooqRepository
                .findById(id, config)
                ?.toTransaction()
        }
    }

    override suspend fun createTransaction(transactionCreate: TransactionCreate): Transaction {
        return transactional { config: Configuration ->
            transactionCreateValidator.validate(transactionCreate, config).failOnError()

            val fromAccount = accountJooqRepository.findById(transactionCreate.fromAcc, config)!!
            val toAccount = accountJooqRepository.findById(transactionCreate.toAcc, config)!!

            fromAccount.balance -= transactionCreate.amount
            toAccount.balance += transactionCreate.amount

            val txId = generateUniqueTxId(config)
            val transactionForInsert = transactionJooqRepository.insert(transactionCreate.toTransactionsRecord(txId), config)

            val updateFromCount = accountJooqRepository.updateBalanceForVersion(fromAccount, config)
            // destination account can be updated without optimistic lock as it is increasing balance
            val updateToCount = accountJooqRepository.updateBalance(toAccount, config)
            require(updateToCount > 0)

            if (updateFromCount == 0) {
                throw AccountStateChangedException()
            }

            transactionForInsert.toTransaction()
        }
    }

    override suspend fun publishTransaction(transactionCreate: TransactionCreate) {
        transactional { config: Configuration ->
            transactionCreateValidator.validate(transactionCreate, config).failOnError()
        }

        val toTransactionCreatedEvent = transactionCreate.toTransactionCreatedEvent()
        kafkaProducerService.sendMessages(
            "transactions",
            toTransactionCreatedEvent.fromAcc,
            ObjectMapperConfiguration.jacksonObjectMapper.writeValueAsString(toTransactionCreatedEvent),
        )
//        eventPublisherService.publishEvent(transactionCreate.toTransactionCreatedEvent())

    }

    private suspend fun generateUniqueTxId(configuration: Configuration): UUID {
        var txIdCandidate = UUID.randomUUID()

        var isGenerated = false
        while (!isGenerated) {
            val tx = transactionJooqRepository.findById(txIdCandidate, configuration)
            if(tx == null) {
                isGenerated = true
            } else {
                txIdCandidate = UUID.randomUUID()
            }
        }

        return txIdCandidate
    }
}