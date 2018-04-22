package com.quxian.lib.alarmmanager.clock;

import android.app.Application;
import android.content.Context;

/**
 * Created by quxia on 2018/4/22.
 */

public class MyApplication extends Application{

    public static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = getApplicationContext();
    }
}
