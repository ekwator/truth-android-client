package com.truth.training.client.crypto

import org.junit.Assert.assertTrue
import org.junit.Test

class CryptoManagerTest {
    @Test
    fun sign_and_verify() {
        val message = "hello"
        val sig = CryptoManager.signMessage(message)
        val pubB64 = CryptoManager.getPublicKeyBase64()
        val pub = CryptoManager.decodePublicKeyFromBase64(pubB64)
        assertTrue(CryptoManager.verifySignature(message, sig, pub))
    }
}


