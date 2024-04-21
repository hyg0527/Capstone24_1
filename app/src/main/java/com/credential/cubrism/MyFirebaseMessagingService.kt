package com.credential.cubrism

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // FCM 토큰을 DataStore에 저장
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notification: RemoteMessage.Notification? = remoteMessage.getNotification()
        if (notification != null) {
            Log.d("테스트", "알림 Title: " + notification.title)
            Log.d("테스트", "알림 Body: " + notification.body)

            // TODO: 알림 띄우기

            // TODO: Room에 알림 저장하기
        }
    }
}
