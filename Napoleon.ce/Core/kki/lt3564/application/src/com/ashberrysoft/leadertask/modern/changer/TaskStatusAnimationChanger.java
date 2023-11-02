package com.ashberrysoft.leadertask.modern.changer;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.SelectionKeeper;
import com.ashberrysoft.leadertask.content_providers.LionMetaData.LTaskContract;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.domains.ordinary.TaskFile;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.helper.TaskNotifyHelper;
import com.ashberrysoft.leadertask.modern.helper.TaskSaveHelper;
import com.ashberrysoft.leadertask.utils.Utils;

import static com.ashberrysoft.leadertask.R.string.task;

public class TaskStatusAnimationChanger extends Thread {
    public static String COMPLETE_EVENT = "TaskStatusAnimationChanger.COMPLETE";

    // BASE
    private final Context mContext;
    private final LTask mTaskOld;
    private final WeakReference<ImageView> mIv;
    private TaskStatus mNewStatus;

    // VALUE's
    private final String mCurrentUser;
    private final boolean mThemeDark;

    private boolean mBeginAnimation;
    private Runnable mRunnableStartAnimation;
    private ContentValues mContentValues;

    // ANIMATION's
    private Animation mAnimationBegin;
    private Animation mAnimationEnd;

    public TaskStatusAnimationChanger(Context context, LTask task, ImageView iv, TaskStatus status) {
        super(TaskStatusAnimationChanger.class.getSimpleName());

        mContext = context.getApplicationContext();
        mTaskOld = task.clone();
        mIv = new WeakReference<ImageView>(iv);
        mNewStatus = status;

        final LTSettings settings = LTSettings.getInstance();
        mCurrentUser = settings.getUserName();
        mThemeDark = settings.isThemeDark();
    }

    @Override
    public void run() {
        super.run();

        final long start = System.currentTimeMillis();
        try {
            process();

        } catch (Exception e) {
            Utils.toLog(e);

        } finally {
            Utils.toLog("<><><> TaskStatusAnimationChanger time = " + (System.currentTimeMillis() - start));
        }

        if (mContext != null){
            Intent i = new Intent(COMPLETE_EVENT);
            mContext.sendBroadcast(i);
        }
    }

    private void process() throws Exception {
        if (mNewStatus == null) {
            final TaskStatus oldStatus = TaskStatus.getTaskStatus(mTaskOld.getStatus());

            if (mCurrentUser.equals(mTaskOld.getEmailCustomer())) {
                switch (oldStatus) {
                case COMPLETED:
                    mNewStatus = TaskStatus.NOT_BEGIN;
                    break;

                case NOTE:
                    break;

                default:
                    mNewStatus = TaskStatus.COMPLETED;
                    break;
                }

            } else if (mCurrentUser.equals(mTaskOld.getEmailPerformer())) {
                switch (oldStatus) {
                case READY:
                    mNewStatus = TaskStatus.NOT_BEGIN;
                    break;

                case COMPLETED:
                case CANCELLED:
                case NOTE:
                    break;

                default:
                    mNewStatus = TaskStatus.READY;
                    break;
                }
            }
        }

        if (mNewStatus != null) {
            final LTask taskNew = mTaskOld.clone();

            taskNew.setUsnEntity(0);
            taskNew.setStatus(mNewStatus.getCode());
            taskNew.setUsnFieldStatus(taskNew.getUsnFieldStatus() + 1);

            mContentValues = mTaskOld.getDifference(taskNew);
            if(taskNew.getStatus() == Status.NOTE.getStatusCode() || mTaskOld.getStatus() == Status.NOTE.getStatusCode()) {
                updateTask();
            }

            //new TaskStatusChanger(mContext, taskNew, mTaskOld).run();
            new TaskSaveHelper(false, mContext, taskNew, false, null, mTaskOld, 0, new ArrayList<TaskFile>(0), new ArrayList<TaskFile>(0), false).run();


//            TaskNotifyHelper.getInstance(mContext).updateTaskNotify(taskNew);
            final ImageView iv = mIv.get();
            if (iv == null) {
                updateTask();

            } else {
                mAnimationBegin = AnimationUtils.loadAnimation(mContext, R.anim.status_animation_after_change_image);
                mAnimationBegin.setAnimationListener(getBeginAnimationListener());

                //mAnimationEnd = AnimationUtils.loadAnimation(mContext, R.anim.status_animation_after_change_image);
                //mAnimationEnd.setAnimationListener(getEndAnimationListener());

                mBeginAnimation = true;
                iv.post(getStartAnimationRunnable());
            }
        }

        //Utils.startSync( (LTApplication) mContext.getApplicationContext());
    }

    private void updateTask() {
        mContext.getContentResolver().update(LTaskContract.CONTENT_URI,//
                mContentValues, SelectionKeeper.equals(null, LTaskContract._ID, mTaskOld.getIdTask()), null);
    }

    private AnimationListener getBeginAnimationListener() {
        return new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                final ImageView iv = mIv.get();
                if (iv != null) {
                    iv.post(getSetImageResourceRunnable());
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                final ImageView iv = mIv.get();
                if (iv != null) {
                    mBeginAnimation = false;
                    //iv.post(getStartAnimationRunnable());
                }
            }
        };
    }

    private AnimationListener getEndAnimationListener() {
        return new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                updateTask();
            }
        };
    }

    private Runnable getStartAnimationRunnable() {
        if (mRunnableStartAnimation == null) {
            mRunnableStartAnimation = new Runnable() {
                @Override
                public void run() {
                    final ImageView iv = mIv.get();
                    if (iv != null) {
                        iv.startAnimation(mBeginAnimation ? mAnimationBegin : mAnimationEnd);
                            }
                }
            };
        }
        return mRunnableStartAnimation;
    }

    private Runnable getSetImageResourceRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                final ImageView iv = mIv.get();
                if (iv != null) {
                    iv.setImageResource(mThemeDark ? mNewStatus.getResIdWhite() : mNewStatus.getResId());
                }
            }
        };
    }
}