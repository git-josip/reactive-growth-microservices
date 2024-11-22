package com.finmid.backendinterview.utils

import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactory
import org.springframework.core.io.Resource
import org.springframework.r2dbc.connection.ConnectionFactoryUtils
import org.springframework.r2dbc.connection.init.ScriptUtils
import reactor.core.publisher.Mono

object SqlTestUtils {
    fun executeTestSqlScriptBlocking(connectionFactory: ConnectionFactory, sqlScript: Resource) {
        // TODO: @Sql still does not work with R2DBC. Until it is supported this is temporary solution
        // Github Issue: https://github.com/spring-projects/spring-framework/issues/33531
        Mono.from(ConnectionFactoryUtils.getConnection(connectionFactory))
            .flatMap { connection: Connection ->
                ScriptUtils.executeSqlScript(connection, sqlScript)
            }
            .block()
    }
}