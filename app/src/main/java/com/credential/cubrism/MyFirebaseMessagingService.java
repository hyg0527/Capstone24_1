package com.credential.cubrism;

import static androidx.appcompat.resources.Compatibility.Api18Impl.setAutoCancel;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "New token: " + token);
        //token을 서버로 전송
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //수신한 메시지를 처리
        RemoteMessage.Notification notification = remoteMessage.getNotification();
        if (notification != null) {
            Log.d("FCM", "Message Notification Title: " + notification.getTitle());
            Log.d("FCM", "Message Notification Body: " + notification.getBody());

            // 알림 채널 생성
            createNotificationChannel();

            // 알림을 생성
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id")
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)  // 기본 Android 아이콘 사용
                    .setContentTitle(notification.getTitle())  // 알림 제목 설정
                    .setContentText(notification.getBody())  // 알림 내용 설정
                    .setPriority(NotificationCompat.PRIORITY_HIGH)  // 알림 우선순위 설정
                    .setAutoCancel(true);  // 알림을 탭하면 자동으로 사라지게 설정

            // 알림을 표시
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(123, builder.build());
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "your_channel_name";
            String description = "your_channel_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("your_channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
