package com.ashberrysoft.leadertask.domains.ordinary;

import com.ashberrysoft.leadertask.R;

/**
 * Перечисление, содержащее все возможные статусы задачи
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public enum Status {
    TASK_NOT_BEGIN(0, R.drawable.status0, R.drawable.status0_small, R.drawable.status0_small_white,
            R.drawable.status0_white, R.string.task_not_begin, R.drawable.status_repeat_0, R.drawable.status_repeat_0w), //
    TASK_COMPLETED(1, R.drawable.status1, R.drawable.status1_small, R.drawable.status1_small_white,
            R.drawable.status1_white, R.string.task_completed, R.drawable.status_repeat_1, R.drawable.status_repeat_1w), //
    NOTE(3, R.drawable.status3, R.drawable.status3_small, R.drawable.status3_small_white, R.drawable.status3_white,
            R.string.note, R.drawable.status_repeat_3, R.drawable.status_repeat_3w), //
    TASK_IN_WORK(4, R.drawable.status4, R.drawable.status4_small, R.drawable.status4_small_white,
            R.drawable.status4_white, R.string.task_in_work, R.drawable.status_repeat_4, R.drawable.status_repeat_4w), //
    TASK_READY(5, R.drawable.status5, R.drawable.status5_small, R.drawable.status5_small_white,
            R.drawable.status5_white, R.string.task_ready, R.drawable.status_repeat_5, R.drawable.status_repeat_5w), //
    TASK_PAUSED(6, R.drawable.status6, R.drawable.status6_small, R.drawable.status6_small_white,
            R.drawable.status6_white, R.string.task_paused, R.drawable.status_repeat_6, R.drawable.status_repeat_6w), //
    TASK_CANCELLED(7, R.drawable.status7, R.drawable.status7_small, R.drawable.status7_small_white,
            R.drawable.status7_white, R.string.task_cancelled, R.drawable.status_repeat_7, R.drawable.status_repeat_7w), //
    TASK_REJECTED(8, R.drawable.status8, R.drawable.status8_small, R.drawable.status8_small_white,
            R.drawable.status8_white, R.string.task_rejected, R.drawable.status_repeat_8, R.drawable.status_repeat_8w), //
    TASK_REFINE(9, R.drawable.status9, R.drawable.status9_small, R.drawable.status9_small_white,
            R.drawable.status9_white, R.string.task_refine, R.drawable.status_repeat_9, R.drawable.status_repeat_9w);

    private int mStatusCode;
    private int mResId;
    private int mResIdSmall;
    private int mResIdSmallWhite;
    private int mResIdWhite;
    private int mTextId;
    private int mSeriesResId;
    private int mSeriesWhiteResId;

    // enumeration constructor
    Status(int statusCode, int resId, int resIdSmall, int resIdSmallWhite, int resIdWhite, int textId, int seriesResId,
            int seriesWhiteResId) {
        mStatusCode = statusCode;
        mResId = resId;
        mResIdSmall = resIdSmall;
        mResIdSmallWhite = resIdSmallWhite;
        mResIdWhite = resIdWhite;
        mTextId = textId;
        mSeriesResId = seriesResId;
        mSeriesWhiteResId = seriesWhiteResId;
    }

    // getter for status code
    public int getStatusCode() {
        return mStatusCode;
    }

    public static Status getStatusByCode(String s) {
        return getStatusByCode(Integer.parseInt(s));
    }

    public static Status getStatusByCode(int code) {
        for (Status status : values()) {
            if (code == status.getStatusCode()) {
                return status;
            }
        }
        return null;
    }

    /**
     * method that defining if task completed or not depending on task status and task customer
     * 
     * @param customer
     *            - task customer
     * @param userName
     *            - current user name
     * @return true - if task is completed false - if task is not completed
     * 
     * @author Vadim Oleynik <vadim.welldone@gmail.com>
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     */
    public boolean isCompleted(String customer, String userName) {
        return (this == Status.TASK_COMPLETED || this == Status.TASK_CANCELLED || (!customer.equals(userName) && (this == Status.TASK_READY || this == Status.TASK_REJECTED)));
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