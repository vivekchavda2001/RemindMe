package com.example.remindme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBrodcast extends BroadcastReceiver {
    private final String CHANNEL_ID = "Personal Notification";
    private final int NOTIFICATION_ID = 0;
    String message,requestId;
    DatabaseHelper databaseHelper;
    String GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL";


    @Override
    public void onReceive(Context context, Intent intent) {


        databaseHelper = new DatabaseHelper(context);
        Uri alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        Intent intent1 = new Intent(context,MainActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addParentStack(MainActivity.class);
        taskStackBuilder.addNextIntent(intent1);
        PendingIntent intent2 = taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);
        message = intent.getStringExtra("reminder");
        requestId = intent.getStringExtra("requestCode");
        //Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
        deleteReminder(requestId);
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("my_channel_01","hello", NotificationManager.IMPORTANCE_HIGH);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.setContentTitle("Reminder")
                .setContentText(message).setAutoCancel(true)
                .setSound(alarmsound).setSmallIcon(R.drawable.logo)
                .setContentIntent(intent2)
                .setChannelId("my_channel_01")
                .build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(1, notification);


//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
//                .setSmallIcon(R.drawable.logo)
//                .setContentTitle(message).setSound(alarmsound).setContentIntent(intent2)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setGroup(GROUP_KEY_WORK_EMAIL);
//        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//        notificationManagerCompat.notify(NOTIFICATION_ID,builder.build());


    }
    public void deleteReminder(String id){
        databaseHelper.mAfterDeleteReminder(id);
    }


}
