package com.nitj.nitjadminapp.firebaseNotificationJava;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nitj.nitjadminapp.R;
import com.nitj.nitjadminapp.screens.DeleteNotice;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class FirebaseServiceJava extends FirebaseMessagingService {

    private final String CHANNEL_ID = "channelId";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Intent intent = new Intent(this, DeleteNotice.class);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel(manager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent intent1 = PendingIntent.getActivities(this,0,new Intent[]{intent},PendingIntent.FLAG_ONE_SHOT);
        Notification notification;

        String title = message.getData().get("title");
        String body = message.getData().get("message");
        String image = message.getData().get("image");
        Bitmap bitmapImage = getBitmapFromUrl(image);



        NotificationCompat.BigPictureStyle notificationStyle = new NotificationCompat.BigPictureStyle();
        notificationStyle.setSummaryText(body);
        notificationStyle.bigPicture(bitmapImage);

            notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.logo)
                    .setAutoCancel(true)
                    .setContentIntent(intent1)
                    .setLargeIcon(bitmapImage)
                    .setStyle(notificationStyle)
                    .build();

        manager.notify(notificationId,notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager manager) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,"ChannelName",NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("MyDescription");
        channel.enableLights(true);
        channel.setLightColor(Color.WHITE);
        manager.createNotificationChannel(channel);
    }

    public Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
