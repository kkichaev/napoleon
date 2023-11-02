package com.ashberrysoft.leadertask.data_providers.network;

import android.content.Context;

import com.ashberrysoft.leadertask.domains.ordinary.LeaderTaskUser;
import com.ashberrysoft.leadertask.utils.CursorySyncLogger;

import java.io.Serializable;
import java.util.Date;

public abstract class BaseTimeSOAPall<T extends Serializable> extends BaseSOAPall<T> {

    private static final long serialVersionUID = 1L;

    private static final String SEND = "send > ";
    private static final String WORK = "work < ";

    private CursorySyncLogger mSyncLogger;

    public BaseTimeSOAPall(Context context, String methodName, LeaderTaskUser user) {
        super(context, methodName, user);
        mSyncLogger = CursorySyncLogger.getInstance(mContext);
    }

    @Override
    protected void preParseResponse() {
        mSyncLogger.toLog(SEND + mMethodName, new Date(System.currentTimeMillis() - mRequestStart));
        mRequestStart = System.currentTimeMillis();
    }

    @Override
    protected void onErrorParseResponse(Throwable e) {
        mSyncLogger.toLog(e);
    }

    @Override
    protected void postParseResponse() {
        mSyncLogger.toLog(WORK + mMethodName, new Date(System.currentTimeMillis() - mRequestStart));
    }
}