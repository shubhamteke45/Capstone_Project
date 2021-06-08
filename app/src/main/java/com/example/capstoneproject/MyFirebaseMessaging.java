package com.example.capstoneproject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.capstoneproject.activities.OrderDetailsSellerActivity;
import com.example.capstoneproject.activities.OrderDetailsUserAcitivty;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "MY_NOTIFICATION_CHANNEL_ID";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //all notification will be received here

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //get data from notification
        String notificationType = remoteMessage.getData().get("notificationType");

        if(notificationType.equals("NewOrder")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationDescription");

            if(firebaseUser != null && firebaseAuth.getUid().equals(sellerUid)){
                showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationDescription, notificationType);
            }
        }
        if(notificationType.equals("OrderStatusChanged")){
            String buyerUid = remoteMessage.getData().get("buyerUid");
            String sellerUid = remoteMessage.getData().get("sellerUid");
            String orderId = remoteMessage.getData().get("orderId");
            String notificationTitle = remoteMessage.getData().get("notificationTitle");
            String notificationDescription = remoteMessage.getData().get("notificationDescription");

            if(firebaseUser != null && firebaseAuth.getUid().equals(buyerUid)){
                showNotification(orderId, sellerUid, buyerUid, notificationTitle, notificationDescription, notificationType);
            }
        }
    }

    private void showNotification(String orderId, String sellerId, String buyerId, String notificationTitle, String notificationDescription, String notificationType){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        //id for notification, random
        int notificationID = new Random().nextInt(3000);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            setupNotificationChannel(notificationManager);
        }

        Intent intent = null;
        if(notificationType.equals("NewOrder")){
            intent = new Intent(this, OrderDetailsSellerActivity.class);
            intent.putExtra("orderId",orderId);
            intent.putExtra("orderBy",buyerId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }
        else if(notificationType.equals("OrderStatusChanged")){
            intent = new Intent(this, OrderDetailsUserAcitivty.class);
            intent.putExtra("orderId",orderId);
            intent.putExtra("orderTo",sellerId);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.food);

        //sound of notification
        Uri notificationSounUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.food)
                .setLargeIcon(largeIcon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationDescription)
                .setSound(notificationSounUri)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //show notification
        notificationManager.notify(notificationID, notificationBuilder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupNotificationChannel(NotificationManager notificationManager) {
        CharSequence channelName = "Some Sample Text";
        String channelDescription = "Channel Description here";

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setDescription(channelDescription);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        if(notificationManager != null){
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
