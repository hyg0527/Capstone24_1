package com.credential.cubrism.model.service

import com.credential.cubrism.BuildConfig
import com.credential.cubrism.model.dto.ChatRequestDto
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class StompClient {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val springUrl = BuildConfig.SPRING_URL
    private val serverUrl = springUrl.replace("http://", "ws://").run {
        if (this.endsWith("/")) "${this}ws" else "${this}/ws"
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

    fun sendMessage(studygroupId: Long, chatRequestDto: ChatRequestDto) {
        val gson = Gson()
        val chatRequestJson = gson.toJson(chatRequestDto)

        val stompMessage = "SEND\n" +
                "destination:/app/sendmessage/$studygroupId\n" +
                "\n" +
                chatRequestJson +
                "\u0000"

        webSocket?.send(stompMessage)
    }
}