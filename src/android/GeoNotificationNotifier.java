package com.cowbell.cordova.geofence;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import mx.grupodp.dp.R;

public class GeoNotificationNotifier {
    private NotificationManager notificationManager;
    private Context context;
    private BeepHelper beepHelper;
    private Logger logger;

    public GeoNotificationNotifier(NotificationManager notificationManager, Context context) {
        this.notificationManager = notificationManager;
        this.context = context;
        this.beepHelper = new BeepHelper();
        this.logger = Logger.getLogger();
    }

    protected Bitmap getBitmapUrl(String imageUrl, Notification notification) {
        InputStream in;
        Bitmap bitmap = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            in = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(in);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("debug_cordova",e.getMessage());
            logger.log(Log.DEBUG, e.getMessage());
            notification.icon = "res://icon_default";
            notification.getLargeIcon();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.i("debug_cordova",e.getMessage());
            logger.log(Log.DEBUG, e.getMessage());
            notification.icon = "res://icon_default";
            notification.getLargeIcon();
        }
        return bitmap;
    }

    public void notify(Notification notification) {
        notification.setContext(context);
        Log.i("debug_cordova",notification.icon);
        Bitmap largeIcon = notification.getLargeIcon();
        if (notification.icon.contains("http")) {
            largeIcon = getBitmapUrl(notification.icon,notification);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setVibrate(notification.getVibrate())
                .setSmallIcon(R.drawable.ic_stat_onesignal_default)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getText());

        if (notification.openAppOnClick) {
            String packageName = context.getPackageName();
            Intent resultIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);

            if (notification.data != null) {
                resultIntent.putExtra("geofence.notification.data", notification.getDataJson());
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    notification.id, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }
        try {
            Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notificationSound);
            r.play();
        } catch (Exception e) {
            beepHelper.startTone("beep_beep_beep");
            e.printStackTrace();
        }
        notificationManager.notify(notification.id, mBuilder.build());
        logger.log(Log.DEBUG, notification.toString());
    }
}
