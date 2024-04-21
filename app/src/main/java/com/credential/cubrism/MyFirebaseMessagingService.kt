package com.credential.cubrism

import com.credential.cubrism.view.utils.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()
    private lateinit var notification: Notification

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // FCM 토큰을 DataStore에 저장
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        notification = Notification(applicationContext)

        remoteMessage.data.let { data ->
            val title = data["title"] ?: ""
            val body = data["body"] ?: ""

            // TODO: 알림 띄우기
            notification.deliverNotification(title, body)

            // TODO: Room에 알림 저장하기

        }
    }
}
