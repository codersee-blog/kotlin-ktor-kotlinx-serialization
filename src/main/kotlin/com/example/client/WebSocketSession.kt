package com.example.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic


object WebSocketSession {

    @Serializable
    abstract class Message {
        abstract val content: String
    }

    @Serializable
    @SerialName("text")
    class TextMessage(override val content: String) : Message()

    @Serializable
    @SerialName("system")
    class SystemMessage(override val content: String, val systemInfo: String) : Message()

    private val module = SerializersModule {
        polymorphic(Message::class) {
            subclass(TextMessage::class, TextMessage.serializer())
            subclass(SystemMessage::class, SystemMessage.serializer())
        }
    }
    val messagesFormat = Json {
        serializersModule = module
    }

    private val client = HttpClient(CIO).config {
        install(WebSockets)
    }

    suspend fun connectWebSocket() {
        client.webSocket(
            host = "0.0.0.0",
            port = 8080,
            path = "/hello"
        ) {
            launch { sendMessage(TextMessage("Good morning!")) }
            launch { receiveMessage() }
            delay(2000)
            launch { sendMessage(TextMessage("Hello!")) }
            launch { receiveMessage() }
            delay(2000)
            println("Connection closed. Goodbye!")
            client.close()
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private suspend fun DefaultClientWebSocketSession.receiveMessage() {
        try {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                val deserializedMessage: Message =
                    messagesFormat.decodeFromStream(message.data.inputStream())
                println("${deserializedMessage.content} // ${(deserializedMessage as? SystemMessage)?.systemInfo}")
            }
        } catch (e: Exception) {
            println("Error while receiving: " + e.localizedMessage)
        }
    }

    private suspend fun DefaultClientWebSocketSession.sendMessage(message: Message) {
        val serializedMessage = messagesFormat.encodeToString(message)
        try {
            send(serializedMessage)
        } catch (e: Exception) {
            println("Some error occur: " + e.localizedMessage)
            return
        }
    }
}