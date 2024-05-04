package com.credential.cubrism.model

import com.credential.cubrism.model.dto.ChatRequest
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import java.io.FileInputStream
import java.util.Properties

class StompClient {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val properties = Properties().apply {
        load(FileInputStream("local.properties"))
    }
    private val springUrl = properties.getProperty("SPRING_URL")
    private val serverUrl = springUrl.replace("http://", "ws://").run {
        if (this.endsWith("/")) this + "ws" else this + "/ws"
    }

    fun connect() {
        val request = Request.Builder().url(serverUrl).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
                super.onOpen(webSocket, response)
                // WebSocket 연결이 열렸을 때의 동작
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                // 서버로부터 메시지를 받았을 때의 동작
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                // 서버로부터 바이너리 메시지를 받았을 때의 동작
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
                super.onFailure(webSocket, t, response)
                // WebSocket 연결이 실패했을 때의 동작
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, null)
    }

    fun sendMessage(studygroupId: Long, chatRequest: ChatRequest) {
        val gson = Gson()
        val chatRequestJson = gson.toJson(chatRequest)

        val stompMessage = JSONObject()
        stompMessage.put("destination", "/app/sendmessage/$studygroupId")
        stompMessage.put("body", JSONObject(chatRequestJson))
        webSocket?.send(stompMessage.toString())
    }
}