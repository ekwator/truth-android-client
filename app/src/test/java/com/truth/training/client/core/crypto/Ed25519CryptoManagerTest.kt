package com.truth.training.client.core.crypto

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test

class Ed25519CryptoManagerTest {
    @Test
    fun sign_verify() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val kp = Ed25519CryptoManager.loadOrCreateKeys(context)
        val msg = "{\"event\":\"truth_claim\",\"value\":1}"
        val sig = Ed25519CryptoManager.signMessage(kp.private, msg)
        assertTrue(Ed25519CryptoManager.verifySignature(kp.public, msg, sig))
    }
}


