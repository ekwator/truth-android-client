package com.truth.training.client

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.truth.training.client.core.crypto.Ed25519CryptoManager
import com.truth.training.client.core.network.PushRequest
import com.truth.training.client.core.network.TruthPushClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class PushTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_test)

        val input = findViewById<EditText>(R.id.inputPayload)
        val send = findViewById<Button>(R.id.btnSend)
        val output = findViewById<TextView>(R.id.output)

        send.setOnClickListener {
            val json = input.text.toString().ifBlank { JSONObject(mapOf("event" to "truth_claim", "value" to 1)).toString() }
            val kp = Ed25519CryptoManager.loadOrCreateKeys(this)
            val sig = Ed25519CryptoManager.signMessage(kp.private, json)
            val pub = Ed25519CryptoManager.getPublicKeyBase64(this)
            val nodeId = android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID)
            val body = PushRequest(nodeId, JSONObject(json).toMap(), sig, pub)
            lifecycleScope.launch(Dispatchers.IO) {
                val bearer = "Bearer " + getSharedPreferences("truth_tokens", MODE_PRIVATE).getString("access", "")
                val resp = TruthPushClient.api.sendEvent(bearer, body)
                launch(Dispatchers.Main) {
                    output.text = "code=${resp.code()} success=${resp.isSuccessful}"
                }
            }
        }
    }
}

private fun JSONObject.toMap(): Map<String, Any> = keys().asSequence().associateWith { get(it) }


