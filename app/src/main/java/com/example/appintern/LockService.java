package com.example.appintern;


import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ServiceInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.ServiceCompat;


public class LockService extends Service {

    BroadcastReceiver receiver;
    View overlayView;
    WindowManager windowManager;
    private NotificationManager notificationManager;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();

        KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock("Overlay");
        lock.disableKeyguard();

        IntentFilter filter = setIntentFilter();



        receiver = new LockReceiver();
        registerReceiver(receiver, filter);
        startForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("test","lock");
        return super.onStartCommand(intent, flags, startId);
    }

    @NonNull
    private IntentFilter setIntentFilter() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        return filter;
    }


    @SuppressLint("ForegroundServiceType")
    private void startForeground() {
        Notification notification = new NotificationCompat.Builder(this,"Channel_ID")
                .setContentTitle(getResources().getString(R.string.app_name))
                .setTicker(getResources().getString(R.string.app_name))
                .setContentText("App is running")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(null)
                .setOngoing(true)
                .build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            NotificationChannel channel = new NotificationChannel("Channel_ID", "KurtChannel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Kurt channel for foreground service notification");

            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            startForeground(9919, notification, 0);
        }
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}

