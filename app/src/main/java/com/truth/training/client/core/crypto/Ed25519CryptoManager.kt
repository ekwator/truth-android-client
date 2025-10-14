package com.truth.training.client.core.crypto

import android.content.Context
import android.util.Base64
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.*
import java.security.spec.X509EncodedKeySpec

object Ed25519CryptoManager {
    private const val KEY_PREFS = "ed25519_keys"
    private const val PUB = "pub"
    private const val PRIV = "priv"
    private var inited = false

    private fun ensureProvider() {
        if (!inited) {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(BouncyCastleProvider())
            }
            inited = true
        }
    }

    fun generateKeyPair(): KeyPair {
        ensureProvider()
        val kpg = KeyPairGenerator.getInstance("Ed25519", BouncyCastleProvider.PROVIDER_NAME)
        return kpg.generateKeyPair()
    }

    @Volatile private var cachedKeys: KeyPair? = null

    fun init(context: Context) {
        if (cachedKeys == null) synchronized(this) {
            if (cachedKeys == null) cachedKeys = loadOrCreateKeys(context)
        }
    }

    fun loadOrCreateKeys(context: Context): KeyPair {
        val prefs = context.getSharedPreferences(KEY_PREFS, Context.MODE_PRIVATE)
        val pubB64 = prefs.getString(PUB, null)
        val privB64 = prefs.getString(PRIV, null)
        return if (pubB64 != null && privB64 != null) {
            val keyFactory = KeyFactory.getInstance("Ed25519", BouncyCastleProvider.PROVIDER_NAME)
            val pub = keyFactory.generatePublic(X509EncodedKeySpec(Base64.decode(pubB64, Base64.NO_WRAP)))
            val priv = java.security.spec.PKCS8EncodedKeySpec(Base64.decode(privB64, Base64.NO_WRAP)).let {
                keyFactory.generatePrivate(it)
            }
            KeyPair(pub, priv)
        } else {
            val kp = generateKeyPair()
            prefs.edit()
                .putString(PUB, Base64.encodeToString(kp.public.encoded, Base64.NO_WRAP))
                .putString(PRIV, Base64.encodeToString(kp.private.encoded, Base64.NO_WRAP))
                .apply()
            kp
        }
    }

    private fun base64EncodeNoPad(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP or Base64.NO_PADDING)

    fun signMessage(privateKey: PrivateKey, message: String): String {
        ensureProvider()
        val sig = Signature.getInstance("Ed25519", BouncyCastleProvider.PROVIDER_NAME)
        sig.initSign(privateKey)
        sig.update(message.toByteArray(Charsets.UTF_8))
        return base64EncodeNoPad(sig.sign())
    }

    fun verifySignature(publicKey: PublicKey, message: String, signatureB64: String): Boolean {
        return try {
            ensureProvider()
            val sig = Signature.getInstance("Ed25519", BouncyCastleProvider.PROVIDER_NAME)
            sig.initVerify(publicKey)
            sig.update(message.toByteArray(Charsets.UTF_8))
            sig.verify(Base64.decode(signatureB64, Base64.NO_WRAP))
        } catch (_: Exception) { false }
    }

    fun getPublicKeyBase64(context: Context? = null): String {
        val kp = cachedKeys ?: context?.let { loadOrCreateKeys(it) } ?: throw IllegalStateException("Ed25519CryptoManager.init(context) not called")
        return base64EncodeNoPad(kp.public.encoded)
    }

    fun decodePublicKeyFromBase64(b64: String): PublicKey {
        ensureProvider()
        val bytes = Base64.decode(b64, Base64.NO_WRAP)
        val spec = X509EncodedKeySpec(bytes)
        return KeyFactory.getInstance("Ed25519", BouncyCastleProvider.PROVIDER_NAME).generatePublic(spec)
    }

    fun signJsonPayload(payload: org.json.JSONObject, context: Context? = null): String {
        val msg = payload.toString()
        val kp = cachedKeys ?: context?.let { loadOrCreateKeys(it) } ?: throw IllegalStateException("Ed25519CryptoManager.init(context) not called")
        return signMessage(kp.private, msg)
    }
}


