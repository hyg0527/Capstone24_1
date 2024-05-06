package com.credential.cubrism.model.service

//import android.util.Log
//import com.credential.cubrism.BuildConfig
//import com.credential.cubrism.model.dto.ChatRequestDto
//import com.credential.cubrism.model.dto.ChatResponseDto
//import com.google.gson.Gson
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.WebSocket
//import okhttp3.WebSocketListener
//import okio.ByteString
//
//class StompClient() {
//    private val client = OkHttpClient()
//    private var webSocket: WebSocket? = null
//    private val springUrl = BuildConfig.SPRING_URL
//    private val serverUrl = springUrl.replace("http://", "ws://").run { if (this.endsWith("/")) "${this}ws" else "${this}/ws" }
//    fun connect(studygroupId: Long) {
//        val request = Request.Builder().url(serverUrl).build()
//        webSocket = client.newWebSocket(request, object : WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: okhttp3.Response) {
//                super.onOpen(webSocket, response)
//                Log.d("소켓 테스트", "Connection opened")
//                // WebSocket 연결이 열렸을 때의 동작
//                subscribe(studygroupId)
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                super.onMessage(webSocket, text)
//                // 서버로부터 메시지를 받았을 때의 동작
//                Log.d("메세지 도착", "Received message: $text")
//                val gson = Gson()
//                val chatResponseDto = gson.fromJson(text, ChatResponseDto::class.java)
//            }
//
//            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
//                super.onMessage(webSocket, bytes)
//                Log.d("바이너리 메세지 도착", "Received message:")
//                // 서버로부터 바이너리 메시지를 받았을 때의 동작
//            }
//
//            override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
//                super.onFailure(webSocket, t, response)
//                // WebSocket 연결이 실패했을 때의 동작
//                Log.e("소켓 테스트", "Connection failed: ${t.message}")
//            }
//        })
//    }
//
//    fun disconnect() {
//        webSocket?.close(1000, null)
//    }
//
//    fun sendMessage(studygroupId: Long, chatRequestDto: ChatRequestDto) {
//        val gson = Gson()
//        val chatRequestJson = gson.toJson(chatRequestDto)
//
//        val stompMessage = "SEND\n" +
//                "destination:/app/sendmessage/$studygroupId\n" +
//                "\n" +
//                chatRequestJson +
//                "\u0000"
//
//        webSocket?.send(stompMessage)
//    }
//
//    fun subscribe(studygroupId: Long) {
//        val stompMessage = "SUBSCRIBE\n" +
//                "id:sub-0\n" +
//                "destination:/topic/public/$studygroupId\n" +
//                "\n" +
//                "\u0000"
//
//        webSocket?.send(stompMessage)
//        Log.d("소켓 테스트", stompMessage)
//    }
//}
import android.util.Log
import com.credential.cubrism.BuildConfig
import com.credential.cubrism.model.dto.ChatRequestDto
import com.credential.cubrism.model.dto.ChatResponseDto
import com.credential.cubrism.view.adapter.ChatAdapter
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent

class StompClient {
    private val springUrl = BuildConfig.SPRING_URL
    private val serverUrl = springUrl.replace("http://", "ws://").run { if (this.endsWith("/")) "${this}ws" else "${this}/ws" }
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl)
    var chatAdapter: ChatAdapter? = null

    fun connect(studygroupId: Long) {
        stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.d("STOMP", "Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> Log.e("STOMP", "Error", lifecycleEvent.exception)
                    LifecycleEvent.Type.CLOSED -> Log.d("STOMP", "Stomp connection closed")
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.e("STOMP", "Server heartbeat failed")
                }
            }

        stompClient.connect()
        subscribe(studygroupId)
    }

    fun disconnect() {
        stompClient.disconnect()
    }

    fun sendMessage(studygroupId: Long, chatRequestDto: ChatRequestDto) {
        val gson = Gson()
        val chatRequestJson = gson.toJson(chatRequestDto)

        stompClient.send("/app/sendmessage/$studygroupId", chatRequestJson)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun subscribe(studygroupId: Long) {
        stompClient.topic("/topic/public/$studygroupId")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { topicMessage ->
                Log.d("STOMP", "Received " + topicMessage.payload)
                val gson = Gson()
                val chatResponseDto = gson.fromJson(topicMessage.payload, ChatResponseDto::class.java)
                chatAdapter?.addItem(chatResponseDto)
            }
    }
}