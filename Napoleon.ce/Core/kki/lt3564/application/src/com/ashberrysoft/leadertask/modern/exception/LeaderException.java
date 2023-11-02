package com.ashberrysoft.leadertask.modern.exception;

import android.text.TextUtils;

import com.ashberrysoft.leadertask.interfaces.LTServerError;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.xml_handlers.ErrorEntity;

public class LeaderException extends RuntimeException {

    private static final long serialVersionUID = 1233L;

    // BASE
    private final ExceptionReason mReason;
    private final Throwable mThrowable;
    private final ErrorEntity mErrorEntity;

    // VALUE's
    private final LeaderExceptionHelper mHelper;
    private final StringBuilder mStringBuilder;

    public static LeaderException create(ExceptionReason reason, Throwable throwable) {
        if (throwable instanceof LeaderException) {
            return (LeaderException) throwable;
        }

        return new LeaderException(reason, throwable, null);
    }

    public static LeaderException create(ExceptionReason reason) {
        return new LeaderException(reason, null, null);
    }

    public static LeaderException create(ErrorEntity entity) {
        return new LeaderException(ExceptionReason.SERVER, null, entity);
    }

    public static LeaderException create(int errorCode) {
        return new LeaderException(ExceptionReason.NULL, null, ErrorEntity.newInstance(errorCode));
    }

    public static LeaderException create(LTServerError serverError) {
        return new LeaderException(ExceptionReason.NULL, null, ErrorEntity.newInstance(serverError));
    }

    private LeaderException(ExceptionReason reason, Throwable throwable, ErrorEntity errorEntity) {
        mReason = reason;
        mThrowable = throwable;
        mErrorEntity = errorEntity;

        mHelper = LeaderExceptionHelper.getInstance();
        mStringBuilder = new StringBuilder();
    }

    public ExceptionReason getReason() {
        return mReason;
    }

    @Override
    public String toString() {
        return getCuteMessage();
    }

    private String getCuteMessage() {
        Utils.clearStringBuilder(mStringBuilder);
        String reason = mHelper.getString(mReason.getErrorResId());
        if (!TextUtils.isEmpty(reason)) {
            mStringBuilder.append(reason);
            mStringBuilder.append(SharedStrings.DOT_C);
            mStringBuilder.append(SharedStrings.NEW_LINE_C);
        }

        if (mErrorEntity != null) {
            if (mErrorEntity.getError() == LTServerError.UNKNOWN) {
                if (TextUtils.isEmpty(mErrorEntity.getMessage())) {
                    mStringBuilder.append(mHelper.getString(LTServerError.UNKNOWN.getResId()));

                } else {
                    mStringBuilder.append(mErrorEntity.getMessage());
                }

            } else {
                mStringBuilder.append(mHelper.getString(mErrorEntity.getError().getResId()));
            }

        } else if (mThrowable != null) {
            String message = mThrowable.getLocalizedMessage();
            if (TextUtils.isEmpty(message)) {
                message = mThrowable.getMessage();
            }
            if (TextUtils.isEmpty(message)) {
                message = mThrowable.getClass().getSimpleName();
            }
            mStringBuilder.append(message);
        }

        return mStringBuilder.toString();
    }

    public int getCode()
    {
        return mErrorEntity.getErrorCode();
    }
}