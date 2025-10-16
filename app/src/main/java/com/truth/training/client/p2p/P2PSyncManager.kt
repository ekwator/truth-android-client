package com.truth.training.client.p2p

import org.json.JSONObject

object P2PSyncManager {
    fun includeCollectiveScore(payload: JSONObject, score: Double?): JSONObject {
        if (score == null) return payload
        val copy = JSONObject(payload.toString())
        copy.put("collective_score", score)
        return copy
    }

    fun mergeCollectiveScore(
        localScore: Double?,
        localCount: Int?,
        remoteScore: Double?,
        remoteCount: Int?
    ): Double? {
        if (localScore == null && remoteScore == null) return null
        if (localScore == null) return remoteScore
        if (remoteScore == null) return localScore
        val lc = (localCount ?: 0).coerceAtLeast(0)
        val rc = (remoteCount ?: 0).coerceAtLeast(0)
        val denom = lc + rc
        if (denom <= 0) return null
        return ((lc * localScore) + (rc * remoteScore)) / denom.toDouble()
    }
}
