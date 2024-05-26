package com.credential.cubrism.model.service

import android.util.Log
import com.credential.cubrism.BuildConfig
import com.credential.cubrism.model.dto.ChatRequestDto
import com.credential.cubrism.model.dto.ChatResponseDto
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.dto.LifecycleEvent

class StompClient {
    private val springUrl = BuildConfig.SPRING_URL
    private val serverUrl = springUrl.replace("http://", "ws://").run { if (this.endsWith("/")) "${this}ws" else "${this}/ws" }
    private val stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, serverUrl)

    private var disposable: Disposable? = null
    private var topicDisposable: Disposable? = null

    fun connect() {
        disposable = stompClient.lifecycle()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> Log.d("STOMP", "Stomp connection opened")
                    LifecycleEvent.Type.ERROR -> Log.e("STOMP", "Error", lifecycleEvent.exception)
                    LifecycleEvent.Type.CLOSED -> Log.d("STOMP", "Stomp connection closed")
                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.e("STOMP", "Server heartbeat failed")
                    null -> {}
                }
            }

        stompClient.connect()
    }

    fun disconnect() {
        disposable?.dispose()
        topicDisposable?.dispose()
        stompClient.disconnect()
    }

    fun sendMessage(studygroupId: Int, chatRequestDto: ChatRequestDto) {
        val gson = Gson()
        val chatRequestJson = gson.toJson(chatRequestDto)

        stompClient.send("/app/sendmessage/$studygroupId", chatRequestJson)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
    }

    fun subscribe(studygroupId: Int, callback: (ChatResponseDto) -> Unit): Disposable {
        return stompClient.topic("/topic/public/$studygroupId")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { topicMessage ->
                val gson = Gson()
                val chatResponseDto = gson.fromJson(topicMessage.payload, ChatResponseDto::class.java)
                callback(chatResponseDto)
            }
    }
}