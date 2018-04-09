package com.bitloor.ggloor.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;

import com.bitloor.ggloor.MainActivity;
import com.bitloor.ggloor.R;
import com.bitloor.ggloor.model.Matches;
import com.bitloor.ggloor.model.Teams;

/**
 * Created by ssaan on 30.05.2017.
 **/

public class NotificationHelper {
    public void notify(Matches match, Teams team, Context context){
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        String strTitle = match.team1.name + " vs " + match.team2.name;
        String strText = match.team1.name + " vs " + match.team2.name + ", BO " + match.colGames + ", "
                + match.dateMatch.getHours() + ":" + match.dateMatch.getMinutes() + " " + match.dateMatch.getDay()
                + "." + match.dateMatch.getMonth() + "." + (match.dateMatch.getYear() + 1900);
        Resources res = context.getResources();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_stat_loyalty)
                // большая картинка
                .setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                //.setTicker(res.getString(R.string.warning)) // текст в строке состояния
                .setTicker(strTitle)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                //.setContentTitle(res.getString(R.string.notifytitle)) // Заголовок уведомления
                .setContentTitle(strTitle)
                //.setContentText(res.getString(R.string.notifytext))
                .setContentText(strText); // Текст уведомления

        // Notification notification = builder.getNotification(); // до API 16
        Notification notification = builder.build();
        notification.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(123, notification);
    }
}
