package com.gradle.develocity.assignment.common.utils

import java.security.spec.KeySpec
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object HashUtils {
    private const val ALGORITHM = "PBKDF2WithHmacSHA512"
    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256

    private val SERVER_API_KEY_SECRET = ApplicationPropertiesUtils.getProperty("server.api-key.secret")

    @OptIn(ExperimentalStdlibApi::class)
    fun generateHash(password: String, salt: String): String {
        val combinedSalt = "$salt$SERVER_API_KEY_SECRET".toByteArray()
        val factory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM)
        val spec: KeySpec = PBEKeySpec(password.toCharArray(), combinedSalt, ITERATIONS, KEY_LENGTH)
        val key: SecretKey = factory.generateSecret(spec)
        val hash: ByteArray = key.encoded

        return hash.toHexString()
    }
}