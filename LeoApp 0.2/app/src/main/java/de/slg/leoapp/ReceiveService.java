package de.slg.leoapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.ExecutionException;

import de.slg.messenger.Message;
import de.slg.messenger.OverviewWrapper;
import de.slg.messenger.ReceiveTask;

public class ReceiveService extends Service {

    private OverviewWrapper wrapper;
    private LoopThread thread;
    private NotificationManager notificationManager;
    private ReceiveTask r;
    private boolean running;
    private long intervall;
    private Bitmap icon;

    public ReceiveService() {
        running = true;
        intervall = 15000;
        wrapper = Utils.getOverviewWrapper();
    }

    class LoopThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            while (running) {
                try {
                    r = new ReceiveTask();
                    r.execute();
                    sleep(intervall);
                    if (r.get())
                        showNotification();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void receive() {
        new ReceiveTask().execute();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Utils.context = getApplicationContext();
        notificationManager = Utils.getNotificationManager();
        icon = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.notification_leo);
        thread = new LoopThread();
        thread.start();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        running = false;
    }

    public void showNotification() {
        Message[] messages = Utils.getMessengerDBConnection().getUnreadMessages();
        String s = "";
        for (Message m : messages)
            s += m.toString() + System.getProperty("line.separator");
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), OverviewWrapper.class), 0);
        Notification notification =
                new NotificationCompat.Builder(getApplicationContext())
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setLargeIcon(icon)
                        .setVibrate(new long[]{200, 100, 200})
                        .setSmallIcon(R.drawable.ic_question_answer_white_24dp)
                        .setContentTitle(getString(R.string.messenger_notification_title))
                        .setContentText(s)
                        .setContentIntent(pendingIntent)
                        .build();
        notificationManager.notify(5453, notification);
    }
}