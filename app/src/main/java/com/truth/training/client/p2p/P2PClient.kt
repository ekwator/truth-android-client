package com.truth.training.client.p2p

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket
import com.truth.training.client.crypto.CryptoManager
import org.json.JSONObject

object P2PClient {
    suspend fun send(host: String, port: Int, json: String, timeoutMs: Int = 5000): String = withContext(Dispatchers.IO) {
        val payload = JSONObject(json)
        val canonical = payload.toString()
        val signature = CryptoManager.signMessage(canonical)
        payload.put("signature", signature)
        payload.put("public_key", CryptoManager.getPublicKeyBase64())
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


