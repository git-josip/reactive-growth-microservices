package com.gradle.develocity.assignment.common.utils

import com.gradle.develocity.assignment.module.timezone.dto.request.GetTimezones
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import java.lang.IllegalStateException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@Execution(ExecutionMode.CONCURRENT)
class StringUtilsTest {
    @Test
    fun `string convert fun should successfully convert Int`() {
        assertEquals(5, "5".convert<Int>())
    }

    @Test
    fun `string convert fun should successfully convert Long`() {
        assertEquals(100L, "100".convert<Long>())
    }

    @Test
    fun `string convert fun should successfully convert Float`() {
        assertEquals(100.23f, "100.23".convert<Float>())
    }

    @Test
    fun `string convert fun should successfully convert Double`() {
        assertEquals(100.23, "100.23".convert<Double>())
    }

    @Test
    fun `string convert fun should successfully convert Boolean true`() {
        assertEquals(true, "true".convert<Boolean>())
    }

    @Test
    fun `string convert fun should successfully convert Boolean false`() {
        assertEquals(false, "false".convert<Boolean>())
    }

    @Test
    fun `string convert fun should fail convert invalid Int false`() {
        assertFailsWith<NumberFormatException>(
            message = "For input string: \"fail\"",
            block = {
                "fail".convert<Int>()
            }
        )
    }

    @Test
    fun `string convert fun should fail convert invalid Long false`() {
        assertFailsWith<NumberFormatException>(
            message = "For input string: \"fail\"",
            block = {
                "fail".convert<Long>()
            }
        )
    }

    @Test
    fun `string convert fun should fail convert invalid Float false`() {
        assertFailsWith<NumberFormatException>(
            message = "For input string: \"fail\"",
            block = {
                "fail".convert<Float>()
            }
        )
    }

    @Test
    fun `string convert fun should fail convert invalid Double false`() {
        assertFailsWith<NumberFormatException>(
            message = "For input string: \"fail\"",
            block = {
                "fail".convert<Double>()
            }
        )
    }

    @Test
    fun `string convert fun should fail to convert unsupported type`() {
        assertFailsWith<IllegalStateException>(
            message = "Converter unavailable for class com.gradle.develocity.assignment.module.timezone.dto.request.GetTimezones",
            block = {
                "fail".convert<GetTimezones>()
            }
        )
    }
}