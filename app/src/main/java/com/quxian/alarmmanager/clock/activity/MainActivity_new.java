package com.quxian.alarmmanager.clock.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.quxian.alarmmanager.clock.R;
import com.quxian.lib.alarmmanager.clock.AlarmManagerUtil;
import com.quxian.alarmmanager.clock.service.BackgroundService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity_new extends AppCompatActivity {

    private TextView textView;
    private final static String TAG = "MainActivity";
    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
    private Timer mTimer;
    private TimerTask run;
    private BackgroundService bgs;
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            bgs = ((BackgroundService.MyBinder) service).getService();
           // showToast("Service started.");
        }

        public void onServiceDisconnected(ComponentName className) {
            bgs = null;
            //showToast("Service disconnected.");
        }
    };

    // 启动并绑定后台服务
    public void doBindService() {
        Intent intent = new Intent(MainActivity_new.this, BackgroundService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void doUnbindService() {
        try {
            unbindService(mConnection);
            stopService(new Intent(MainActivity_new.this, BackgroundService.class));
            bgs = null;
        } catch (Exception e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_new);
        textView = (TextView) findViewById(R.id.textView);

        doBindService();
        refreshTask();
        setAlarm();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(bgs == null){

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
        }
    }


    private void refreshTask() {
        mTimer = new Timer();
        run = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //获取信息并且设置闹钟
                        textView.setText(bgs.getInfoDetails());
                    }
                });
                //设置闹钟
                setAlarm();
            }
        };
        //一分钟获取一次数据
        mTimer.schedule(run, 100, 90000);
    }

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String s = format.format(date);
        Log.e(TAG,s);
        return s;
    }

    private void setAlarm(){
        //设置闹钟
        Date date = new Date();
        String time = getTime(date);
        final String[] times = time.split(":");
        AlarmManagerUtil.setAlarm(MainActivity_new.this,0, Integer.parseInt(times[0]), Integer.parseInt
                (times[1])+1,0,0,"alarm!!!!",2);
        final int i = Integer.parseInt(times[1])+1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast("已设置"+times[0]+":"+i+"的提醒");
            }
        });
    }
}
