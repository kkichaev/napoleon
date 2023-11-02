package com.ashberrysoft.leadertask.views;

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.HomeActivity;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.application.LTSettings.TaskMode;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dataproviders.AbstractDataRequestException;

/**
 * 
 * @since 2014-06-26
 * @author Tregub Artem tregub.artem@gmail.com
 */

@SuppressWarnings("deprecation")
public class TaskViewAnimation implements Runnable {

    // VALUE's
    private HomeActivity mHomeActivity;
    private Context mContext;
    private Task mTask;
    private TaskStatus mStatus;

    private LTSettings mSettings;
    private Handler mHandlerUI;
    private Runnable mAnimationRun;
    private boolean mStart;
    private TaskStatus mNewStatus;
    private TaskStatus mOldStatus;

    private Boolean mRemoveTask;

    // ANIMATION's
    private Animation mAnimationStart;
    private Animation mAnimationEnd;

    // VIEW's
    private ImageView mImageView;

    public static void startAnimation(Context context, ImageView iv, Task task, TaskStatus status) {
        new Thread(new TaskViewAnimation(context, iv, task, status)).start();
    }

    private TaskViewAnimation(Context context, ImageView iv, Task task, TaskStatus status) {
        if (context instanceof HomeActivity) {
            mHomeActivity = (HomeActivity) context;
        }
        mContext = context.getApplicationContext();
        mImageView = iv;
        mTask = task;
        mStatus = status;
    }

    @Override
    public void run() {
        mSettings = LTSettings.getInstance(mContext);
        mHandlerUI = mImageView.getHandler();
        mAnimationRun = getAnimationRunnable();

        mAnimationStart = AnimationUtils.loadAnimation(mContext, R.anim.status_animation_before_change_image);
        mAnimationStart.setAnimationListener(getAnimationListenerStart());

        mAnimationEnd = AnimationUtils.loadAnimation(mContext, R.anim.status_animation_after_change_image);
        mAnimationEnd.setAnimationListener(getAnimationListenerEnd());

        mStart = true;
        mHandlerUI.post(mAnimationRun);
    }

    private AnimationListener getAnimationListenerStart() {
        return new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mHomeActivity.setBlockingProcess(true, this);

                mOldStatus = mTask.getStatusType();
                if (mStatus == null) {
                    if (mSettings.getUserName().equals(mTask.getCustomer())) {
                        switch (mOldStatus) {
                        case COMPLETED:
                            mNewStatus = TaskStatus.NOT_BEGIN;
                            break;

                        case NOTE:
                            mNewStatus = null;
                            break;

                        default:
                            mNewStatus = TaskStatus.COMPLETED;
                            mRemoveTask = mSettings.isMakeTaskHide();
                            break;
                        }
                    }

                    else if (mSettings.getUserName().equals(mTask.getPerformer())) {
                        switch (mOldStatus) {
                        case READY:
                            mNewStatus = TaskStatus.NOT_BEGIN;
                            break;

                        case COMPLETED:
                        case CANCELLED:
                        case NOTE:
                            mNewStatus = null;
                            break;

                        default:
                            mNewStatus = TaskStatus.READY;
                            mRemoveTask = mSettings.isMakeTaskHide();
                            break;
                        }
                    }

                } else {
                    mNewStatus = mStatus;
                    switch (mNewStatus) {
                    case CANCELLED:
                    case COMPLETED:
                        mRemoveTask = mSettings.isMakeTaskHide();
                        break;

                    default:
                        break;
                    }
                }

                if (mNewStatus != null) {
                    mTask.setStatusType(mNewStatus);
                    mTask.setUsnStatus(mTask.getUsnStatus() + 1);
                    mTask.setUsn(0);

                    mHandlerUI.post(getSetImageRunnable());
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                mStart = false;
                mHandlerUI.post(mAnimationRun);
            }
        };
    }

    private AnimationListener getAnimationListenerEnd() {
        return new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mHomeActivity != null) {
                    if (mRemoveTask != null && mRemoveTask) {
                        mHomeActivity.onTaskDeleted(mTask, false, true);
                    } else {
                        mHomeActivity.onTaskChanged(mTask);
                    }
                }
                new Thread(getUpdateTaskRunnable()).start();
            }
        };
    }

    private Runnable getAnimationRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                mImageView.startAnimation(mStart ? mAnimationStart : mAnimationEnd);
            }
        };
    }

    private Runnable getSetImageRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                mImageView.setImageResource(mSettings.isThemeDark() ? mNewStatus.getResIdWhite()//
                        : mNewStatus.getResId());
            }
        };
    }

    private Runnable getUpdateTaskRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                updateTaskStatus();
                if (taskSeriesCalculation()) {
                    mHandlerUI.post(getAddNewTaskRunnable(null));
                }

                mContext = null;
                mImageView = null;
                mTask = null;
                mSettings = null;
            }
        };
    }

    private void updateTaskStatus() {
        try {
            final DbHelper dbHelper = DbHelper.getInstance(mContext);
            dbHelper.editsDueToStatusChanged(mHomeActivity, mTask.getParentId(), mRemoveTask == null ? 1 : -1, false);
            dbHelper.updateTask(mTask, false, false, false);
        } catch (AbstractDataRequestException e) {
            Utils.toLog(e);
        }
    }

    private boolean taskSeriesCalculation() {
        if (mSettings.getUserName().equals(mTask.getCustomer()) && (mNewStatus != null//
                && (mNewStatus == TaskStatus.COMPLETED || mNewStatus == TaskStatus.CANCELLED))) {
            final TaskSeriesCalculator taskSeriesCalculator = new TaskSeriesCalculator(mContext, mTask);
            taskSeriesCalculator.createNextSeriesTask();

            final Task newTask = taskSeriesCalculator.getNewTask();
            if (newTask != null && mHomeActivity != null) {
                if (mSettings.getTaskMode() == TaskMode.TODAY) {
                    final Calendar dayEnd = Calendar.getInstance(TimeZone.getTimeZone(SharedStrings.GMT));
                    if (newTask.getTermBegin().getTime() <= dayEnd.getTimeInMillis()) {
                        mHandlerUI.post(getAddNewTaskRunnable(newTask));
                        return false;
                    }
                } else {
                    mHandlerUI.post(getAddNewTaskRunnable(newTask));
                    return false;
                }
            }
        }
        return true;
    }

    private Runnable getAddNewTaskRunnable(final Task task) {
        return new Runnable() {
            @Override
            public void run() {
                if (task != null) {
                    mHomeActivity.onTaskAdded(task);
                }
                mHomeActivity.setBlockingProcess(false, this);
                mHomeActivity = null;
            }
        };
    }
}