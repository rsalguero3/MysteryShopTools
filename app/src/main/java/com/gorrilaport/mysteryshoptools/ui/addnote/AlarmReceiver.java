package com.gorrilaport.mysteryshoptools.ui.addnote;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.gorrilaport.mysteryshoptools.R;
import com.gorrilaport.mysteryshoptools.model.Note;
import com.gorrilaport.mysteryshoptools.util.Constants;

public class AlarmReceiver extends BroadcastReceiver {
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {


        if (intent !=null && intent.hasExtra(Constants.SERIALIZED_NOTE)){
            String serializedNote = intent.getStringExtra(Constants.SERIALIZED_NOTE);
            if (!TextUtils.isEmpty(serializedNote)){
                buildNotification(serializedNote, context);
            }
        }
    }


    private void buildNotification(String serializedNote, Context context){

        Gson gson = new Gson();
        Note passedInNote = gson.fromJson(serializedNote, Note.class);


        String message =  passedInNote.getContent().substring(0, Math.min(passedInNote.getContent().length(), 50));
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(passedInNote.getTitle())
                        .setSound(alarmSound)
                        .setContentText(message);

        Intent resultIntent = new Intent(context, AddNoteActivity.class);
        if (!TextUtils.isEmpty(serializedNote)){
            resultIntent.putExtra(Constants.SERIALIZED_NOTE, serializedNote);
        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(AddNoteActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(12, notificationBuilder.build());



    }
}
