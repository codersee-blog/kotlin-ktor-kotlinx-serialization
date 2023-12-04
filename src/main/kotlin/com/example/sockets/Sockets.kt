package com.example.sockets

import com.example.client.WebSocketSession
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.decodeFromStream
import java.time.Duration

data class Connection(val session: DefaultWebSocketSession)

@OptIn(ExperimentalSerializationApi::class)
fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/hello") {
            try {
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val deserializedMessage: WebSocketSession.Message =
                        WebSocketSession.messagesFormat.decodeFromStream(frame.data.inputStream())
                    val newMessageText = deserializedMessage.content + " - from Client"
                    val serializedMessage = WebSocketSession.messagesFormat.encodeToString<WebSocketSession.Message>(
                        WebSocketSession.SystemMessage(
                            content = newMessageText,
                            systemInfo = "Important"
                        )
                    )
                    Connection(this).session.send(serializedMessage)
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
    }
}