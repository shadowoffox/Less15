package com.example.lesson15;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.telephony.SmsMessage;

import androidx.core.app.NotificationCompat;

public class SMSReciver extends BroadcastReceiver {

    int messageId = 0;
    static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent != null && intent.getAction() != null){
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[objects.length];
            for (int i=0;i<objects.length;i++){
                messages[i] = SmsMessage.createFromPdu((byte[])objects[i]);
            }
            String smsFromPhone = messages[0].getDisplayOriginatingAddress();
            StringBuilder body = new StringBuilder();
            for (SmsMessage message:messages){
                body.append(message.getMessageBody());
            }
            String bodyText = body.toString();
            makeNote(context,smsFromPhone,bodyText);
        }
    }

    private void makeNote(Context context, String smsFrom, String message){
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            showOldNotifications(context, smsFrom, message);
        } else {
            showNewNotifications(context, message);
        }
    }

    @SuppressLint("NewApi")
    private void showNewNotifications(Context context, String message){

        Intent intent = new Intent(context,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent =  PendingIntent.getActivity(context,12345,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        nBuilder.setSmallIcon(R.mipmap
                .ic_launcher_round)
                .setContentTitle("mimimi")
                .setContentText(message)
                .setAutoCancel(false)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel nChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME" ,importance);

        nChannel.enableLights(true);
        nChannel.setLightColor(Color.RED);
        nChannel.enableVibration(true);
        nManager.createNotificationChannel(nChannel);
        nManager.notify(12345, nBuilder.build());
    }

    private void showOldNotifications(Context context, String smsFrom, String message){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"2")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(String.format("SMS [%s]",smsFrom))
                .setContentText(message);
        Intent intent = new Intent(context, SMSReciver.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntent(intent);
        PendingIntent pending = taskStackBuilder.getPendingIntent(12345,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pending);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++,builder.build());
    }


}


