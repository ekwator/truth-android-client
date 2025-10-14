package com.truth.training.client.p2p

import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import com.truth.training.client.crypto.CryptoManager

class P2PDiscoveryTest {
    @Test
    fun loopback_server_client_exchange() = runBlocking {
        val server = P2PServer(this)
        server.start()
        val port = server.port
        val req = JSONObject().apply { put("action", "ping"); put("node_id", "test") }.toString()
        val resp = P2PClient.send("127.0.0.1", port, req)
        // Ответ формируется TruthCore, здесь проверяем что строка не пустая (интеграционный сценарий)
        assertEquals(true, resp.isNotBlank())
        server.stop()
    }
}


