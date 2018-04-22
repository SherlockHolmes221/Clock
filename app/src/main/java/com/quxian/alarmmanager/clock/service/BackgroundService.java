package com.quxian.alarmmanager.clock.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.quxian.alarmmanager.clock.R;
import com.quxian.alarmmanager.clock.activity.MainActivity_new;


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 *获取服务器数据的进程
 */

public class BackgroundService extends Service {

    private static final String url = "http://120.78.195.149:8080/connect-0.0.1-SNAPSHOT/video";
    private String info = "";
    private static int time = 1;

    private final static String TAG = "BackgroundService";
    public class MyBinder extends Binder {
        public BackgroundService getService() {
            return BackgroundService.this;
        }
    }
    private final IBinder mBinder = new MyBinder();
    private NotificationManager mNM;
    private TimerTask run;
    private Timer mTimer;

    //public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /* 根据https://developer.android.com/guide/components/services.html的描述
    * 启动服务时，先执行onBind() / onStartCommand()方法
    * 前者可以提供绑定并与activity进行交互，后者可以实现一直运行在后台
    * 如果服务是第一次启动，再执行onCreate()方法
    */

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // 获取系统通知服务
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // 显示本服务通知
        showNotification();
        // 设置Timer和TimerTask
        refreshTask();
        return mBinder;
    }

    @Override
    public void onCreate() {
        //super.onCreate();
    }

    private void showNotification() {
        // PendingIntent 用来实现用户点击通知时跳转到拉起本应用
        // FIXME: 点击后又重新创建一个Activity...
        PendingIntent notificationIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity_new.class), 0);

        // 设置通知显示样式的Notification.Builder在API11之前不可用，使用NotificationCompat.Builder取代之
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("BackgroundService")
                .setContentText("getting information...")
                .setContentIntent(notificationIntent)
                .setOngoing(true);
        // 设置服务通知
        mNM.notify(0, mBuilder.getNotification());
        // 进程防杀
        startForeground(0, mBuilder.getNotification());
    }

    @Override
    public boolean onUnbind(Intent intent) {
        super.onUnbind(intent);
        // 销毁通知
        mNM.cancelAll();
        // 取消定时器
        mTimer.cancel();
        mTimer.purge();
        // 返回值为true时，再次bind执行的是onRebind()方法，反之执行onBind()方法
        return false;
    }


    @Override
    public void onDestroy() {
//        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    private void refreshTask() {
        mTimer = new Timer();
        run = new TimerTask() {
            @Override
            public void run() {
                getInfo();
            }

    };
        //一分钟获取一次数据
        mTimer.schedule(run, 0, 60000);
    }

    //从服务器获取信息
    private void getInfo() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                setInfoDetails("setTime: ");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code() == 200){
                    setInfoDetails("setTime: ");
                }

            }
        });
    }

    public void setInfoDetails(String s) {
        this.info = s + time;
        time++;
    }

    public String getInfoDetails() {
        if(info.equals("")){
            return "setTime: " + String.valueOf(1);
        }
        return this.info;
    }

}
