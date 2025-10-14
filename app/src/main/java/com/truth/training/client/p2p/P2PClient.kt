package com.truth.training.client.p2p

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import android.content.Context
import com.truth.training.client.core.crypto.Ed25519CryptoManager
import org.json.JSONObject

object P2PClient {
    suspend fun send(context: Context, host: String, port: Int, json: String, timeoutMs: Int = 5000): String = withContext(Dispatchers.IO) {
        val payload = JSONObject(json)
        val canonical = payload.toString()
        val keyPair = Ed25519CryptoManager.loadOrCreateKeys(context)
        val signature = Ed25519CryptoManager.signMessage(keyPair.private, canonical)
        payload.put("signature", signature)
        payload.put("public_key", Ed25519CryptoManager.getPublicKeyBase64(context))
        val socket = Socket()
        try {
            socket.connect(InetSocketAddress(host, port), timeoutMs)
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            writer.println(payload.toString())
            writer.flush()
            reader.readLine() ?: ""
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }
}


