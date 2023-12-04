package com.example.client

import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        WebSocketSession.connectWebSocket()
    }
}