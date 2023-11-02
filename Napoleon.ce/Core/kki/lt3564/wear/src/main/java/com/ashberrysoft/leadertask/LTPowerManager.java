package com.ashberrysoft.leadertask;

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class LTPowerManager {

    private static final String CLASS_PATH = LTPowerManager.class.getName();
    public static final String WAKE_LOCK_TAG = CLASS_PATH + "WAKE_LOCK_TAG";

    // SINGLETONE
    private static LTPowerManager sInstance;

    // VALUE's
    private PowerManager mPowerManager;
    private WakeLock mWakeLock;

    public static LTPowerManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LTPowerManager.class) {
                if (sInstance == null) {
                    sInstance = new LTPowerManager(context);
                }
            }
        }
        return sInstance;
    }

    private LTPowerManager(Context context) {
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    @SuppressWarnings("deprecation")
    public void sleepLock() {
        if (mWakeLock == null) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, WAKE_LOCK_TAG);
            mWakeLock.acquire();
        }
    }

    public void sleepUnlock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
}