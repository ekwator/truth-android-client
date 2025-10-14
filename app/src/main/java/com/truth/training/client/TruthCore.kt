package com.truth.training.client

object TruthCore {
    init {
        System.loadLibrary("truthcore")
    }

    external fun initNode()
    external fun getInfo(): String
    external fun freeString(ptr: Long)
    external fun processJsonRequest(request: String): String
}
