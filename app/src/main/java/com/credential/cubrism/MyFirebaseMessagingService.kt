package com.credential.cubrism

import com.credential.cubrism.model.entity.NotiEntity
import com.credential.cubrism.model.repository.NotiRepository
import com.credential.cubrism.view.utils.Notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val dataStore = MyApplication.getInstance().getDataStoreRepository()
    private lateinit var notification: Notification

    private val notificationRepository = NotiRepository(MyApplication.getInstance().getNotiDao())

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        super.onCreate()
        notification = Notification(applicationContext)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // FCM 토큰을 DataStore에 저장
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveFcmToken(token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.let { data ->
            val title = data["title"] ?: ""
            val body = data["body"] ?: ""

            // 알림 띄우기
            notification.deliverNotification(title, body)

            //  Room에 알림 저장하기
            coroutineScope.launch {
                notificationRepository.insertNoti(NotiEntity(title = title, body = body))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
