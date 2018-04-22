package com.quxian.lib.alarmmanager.clock;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by quxia on 2018/4/22.
 */

public class AppUtils {
    /**
     * 唤醒手机屏幕并解锁
     */
    public static void wakeUpAndUnlock() {
        // 获取电源管理器对象
        PowerManager pm = (PowerManager) MyApplication.sContext.getSystemService(Context.POWER_SERVICE);
        boolean screenOn = pm.isScreenOn();
        if (!screenOn) {
            // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "ScreenWakeUp");
            // 点亮屏幕
            wl.acquire(5000);
            // 释放
            wl.release();
        }
        // 屏幕解锁
        KeyguardManager keyguardManager = (KeyguardManager) MyApplication.sContext.getSystemService(KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("UnlockScreen");
        // 屏幕锁定
        keyguardLock.reenableKeyguard();
        // 解锁
        keyguardLock.disableKeyguard();
    }
}
