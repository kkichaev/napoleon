package com.ashberrysoft.leadertask.modern.exception;

import android.content.Context;

public class LeaderExceptionHelper {

    private static LeaderExceptionHelper sInstance;
    private final Context mContext;

    public static void init(Context context) {
        if (sInstance == null) {
            sInstance = new LeaderExceptionHelper(context);
        }
    }

    public static LeaderExceptionHelper getInstance() {
        return sInstance;
    }

    private LeaderExceptionHelper(Context context) {
        mContext = context.getApplicationContext();
    }

    public Context getContext() {
        return mContext;
    }

    public String getString(int resId, Object... formatArgs) {
        return mContext.getString(resId, formatArgs);
    }
}