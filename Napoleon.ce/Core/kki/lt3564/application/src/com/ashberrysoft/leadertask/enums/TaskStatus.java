package com.ashberrysoft.leadertask.enums;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;

public enum TaskStatus {

    NOT_BEGIN(0, R.drawable.status0, R.drawable.status0_small, R.drawable.status0_small_white,
            R.drawable.status0_white, R.string.task_not_begin, R.drawable.status_repeat_0, R.drawable.status_repeat_0w), //
    COMPLETED(1, R.drawable.status1, R.drawable.status1_small, R.drawable.status1_small_white,
            R.drawable.status1_white, R.string.task_completed, R.drawable.status_repeat_1, R.drawable.status_repeat_1w), //
    NOTE(3, R.drawable.status3, R.drawable.status3_small, R.drawable.status3_small_white, R.drawable.status3_white,
            R.string.note, R.drawable.status_repeat_3, R.drawable.status_repeat_3w), //
    IN_WORK(4, R.drawable.status4, R.drawable.status4_small, R.drawable.status4_small_white, R.drawable.status4_white,
            R.string.task_in_work, R.drawable.status_repeat_4, R.drawable.status_repeat_4w), //
    READY(5, R.drawable.status5, R.drawable.status5_small, R.drawable.status5_small_white, R.drawable.status5_white,
            R.string.task_ready, R.drawable.status_repeat_5, R.drawable.status_repeat_5w), //
    PAUSED(6, R.drawable.status6, R.drawable.status6_small, R.drawable.status6_small_white, R.drawable.status6_white,
            R.string.task_paused, R.drawable.status_repeat_6, R.drawable.status_repeat_6w), //
    CANCELLED(7, R.drawable.status7, R.drawable.status7_small, R.drawable.status7_small_white,
            R.drawable.status7_white, R.string.task_cancelled, R.drawable.status_repeat_7, R.drawable.status_repeat_7w), //
    REJECTED(8, R.drawable.status8, R.drawable.status8_small, R.drawable.status8_small_white, R.drawable.status8_white,
            R.string.task_rejected, R.drawable.status_repeat_8, R.drawable.status_repeat_8w), //
    REFINE(9, R.drawable.status9, R.drawable.status9_small, R.drawable.status9_small_white, R.drawable.status9_white,
            R.string.task_refine, R.drawable.status_repeat_9, R.drawable.status_repeat_9w);

    final int mCode;
    final int mResId;
    final int mResIdSmall;
    final int mResIdSmallWhite;
    final int mResIdWhite;
    final int mTextId;
    final int mSeriesResId;
    final int mSeriesWhiteResId;

    TaskStatus(int statusCode, int resId, int resIdSmall, int resIdSmallWhite, int resIdWhite, int textId,
            int seriesResId, int seriesWhiteResId) {
        mCode = statusCode;
        mResId = resId;
        mResIdSmall = resIdSmall;
        mResIdSmallWhite = resIdSmallWhite;
        mResIdWhite = resIdWhite;
        mTextId = textId;
        mSeriesResId = seriesResId;
        mSeriesWhiteResId = seriesWhiteResId;
    }

    public static TaskStatus getTaskStatusFromString(String s) {
        return getTaskStatus(Integer.parseInt(s));
    }

    public static TaskStatus getTaskStatus(int code) {
        for (TaskStatus status : values()) {
            if (code == status.getCode()) {
                return status;
            }
        }
        return NOT_BEGIN;
    }

    public static TaskStatus getTaskStatus(LTask task) {
        final int code = task.getStatus();

        for (TaskStatus status : values()) {
            if (code == status.getCode()) {
                return status;
            }
        }
        return NOT_BEGIN;
    }

    public boolean isCompleted(String customer, String currentUser) {
        return this == TaskStatus.COMPLETED || this == TaskStatus.CANCELLED
                || (!customer.equals(currentUser) && (this == TaskStatus.READY || this == TaskStatus.REJECTED));
    }

    public int getCode() {
        return mCode;
    }

    public int getResId() {
        return mResId;
    }

    public int getResIdSmall() {
        return mResIdSmall;
    }

    public int getResIdSmallWhite() {
        return mResIdSmallWhite;
    }

    public int getResIdWhite() {
        return mResIdWhite;
    }

    public int getTextId() {
        return mTextId;
    }

    public int getSeriesResId() {
        return mSeriesResId;
    }

    public int getSeriesWhiteResId() {
        return mSeriesWhiteResId;
    }
}