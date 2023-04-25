package com.example.earlysuraksha;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.earlysuraksha.Dashboard;
import com.example.earlysuraksha.MainActivity;
import com.example.earlysuraksha.R;
import com.example.earlysuraksha.Splash_screen;
import com.google.android.gms.maps.model.Dash;

@RequiresApi(api = Build.VERSION_CODES.O)
public final class WorkerUtils {
    public static boolean isSeen=false;

    public static void makeStatusNotification(String message, Context context) {

        Intent intent = new Intent(context, Dashboard.class).putExtra("yes","true");
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(context,
                    0, intent, PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(context,
                    0, intent, PendingIntent.FLAG_IMMUTABLE);

        }

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name ="Early Suraksha";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("com.example.earlysuraksha.Location", name, importance);
            channel.setDescription(message);
            // Add the channel
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

        }

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "com.example.earlysuraksha.Location")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("New Location Update")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setVibrate(new long[0])
                .setAutoCancel(true)
//                .addAction(R.drawable.ic_launcher_foreground,"Ok",)
                .addAction(R.drawable.ic_launcher_foreground,"See details",pendingIntent);



        // Show the notification
        NotificationManagerCompat.from(context).notify((int) System.currentTimeMillis(), builder.build());
    }

}
