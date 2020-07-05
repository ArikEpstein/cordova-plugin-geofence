package com.cowbell.cordova.geofence;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
// import android.graphics.Bitmap;
// import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import android.os.Build;
import android.util.Log;

// import java.io.IOException;
// import java.io.InputStream;
// import java.net.HttpURLConnection;
// import java.net.MalformedURLException;
// import java.net.URL;


public class GeoNotificationNotifier {
    private NotificationManager notificationManager;
    private Context context;
    private BeepHelper beepHelper;
    private Logger logger;
    private NotificationChannel notificationChannel;

    public GeoNotificationNotifier(NotificationManager notificationManager, Context context) {
        this.notificationManager = notificationManager;
        this.context = context;
        this.beepHelper = new BeepHelper();
        this.logger = Logger.getLogger();

         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
           notificationChannel = new NotificationChannel("channelId", "channelName", NotificationManager.IMPORTANCE_DEFAULT);
           notificationManager.createNotificationChannel(notificationChannel);

             // CharSequence name = getString(R.string.app_name);
            //  NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
        }
    }

//     protected Bitmap getBitmapUrl(String imageUrl, Notification notification) {
//         InputStream in;
//         Bitmap bitmap = null;
//         try {
//             URL url = new URL(imageUrl);
//             HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//             connection.setDoInput(true);
//             connection.connect();
//             in = connection.getInputStream();
//             bitmap = BitmapFactory.decodeStream(in);
//         }
//         catch (MalformedURLException e) {
//             e.printStackTrace();
//             Log.i("debug_cordova",e.getMessage());
//             logger.log(Log.DEBUG, e.getMessage());
//             notification.icon = "res://icon_default";
//             notification.getLargeIcon();
//         }
//         catch (IOException e) {
//             e.printStackTrace();
//             Log.i("debug_cordova",e.getMessage());
//             logger.log(Log.DEBUG, e.getMessage());
//             notification.icon = "res://icon_default";
//             notification.getLargeIcon();
//         }
//         return bitmap;
//     }

   // public void notify(Notification notification, String transition) {
    public void notify(Notification notification) {
        notification.setContext(context);
        Log.i("debug_cordova", notification.icon);

        NotificationCompat.Builder mBuilder = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mBuilder = new NotificationCompat.Builder(context, notificationChannel.getId());
        } else {
            mBuilder = new NotificationCompat.Builder(context);
        }
        mBuilder.setVibrate(notification.getVibrate())
                .setSmallIcon(notification.getSmallIcon())
                .setLargeIcon(notification.getLargeIcon())
                .setAutoCancel(true)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getText());

//         Bitmap largeIcon = notification.getLargeIcon();
//         if (notification.icon.contains("http")) {
//             largeIcon = getBitmapUrl(notification.icon,notification);
//         }

//         NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
//                 .setVibrate(notification.getVibrate())
//                 .setLargeIcon(largeIcon)
//                 .setAutoCancel(true)
//                 .setContentTitle(notification.getTitle())
//                 .setContentText(notification.getText());

        if (notification.openAppOnClick) {
        // FIREBASE X
        // PackageManager pm = context.getPackageManager();
        // Intent launchIntent = pm.getLaunchIntentForPackage(context.getPackageName());
        // launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Bundle data = intent.getExtras();
        //  if(!data.containsKey("messageType")) data.putString("messageType", "notification");
        // data.putString("tap", FirebasePlugin.inBackground() ? "background" : "foreground");
        //  Log.d(FirebasePlugin.TAG, "OnNotificationOpenReceiver.onReceive(): "+data.toString());
        //   FirebasePlugin.sendMessage(data, context);
       // launchIntent.putExtras(data);
       // context.startActivity(launchIntent);

            String packageName = context.getPackageName();
            Intent resultIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);

            if (notification.data != null) {
                resultIntent.putExtra("geofence.notification.data", notification.getDataJson());
            }

//             TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//             stackBuilder.addNextIntent(resultIntent);
//             PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
//                     notification.id, PendingIntent.FLAG_UPDATE_CURRENT);

          resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

           PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(context,
                         notification.id,
                         resultIntent,
                         PendingIntent.FLAG_UPDATE_CURRENT
                    );

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
