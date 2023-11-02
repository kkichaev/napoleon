package com.ashberrysoft.leadertask.views;

import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.TaskAdapter;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * Отображение задачи
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class SubtaskView extends LinearLayout implements View.OnClickListener {

    public interface OnTaskStatusListener {
        public void onTaskStatusClick(Task task);

        public void onTaskViewClick(Task task);
    }

    private static final int BADGE_GREEN_COLOR = 0x88008800;
    private static final int BADGE_BROWN_COLOR = Color.argb(204, 147, 92, 11);
    private static final String LABEL_DIVIDER = ": ";

    // VIEW's
    // TASK STATUS, SUBTASKS COUNT, TASK NAME
    private ImageView mIvStatus;
    private BadgeView mBvSubtasks;
    private TextView mTvTaskName;

    // STATUS LINE
    private ImageView mIvUser;
    private TextView mTvUser;
    private View mIvTerm;
    private TextView mTvTerm;
    private View mIvTermCustomer;
    private TextView mTvTermCustomer;
    private View mIvAttachedFiles;
    private View mIvTaskComment;
    private View mIvMessages;

    // TASK LABEL's
    private TextView[] mModifiedViews;

    // VALUE's
    private LTSettings mSettings;
    private Task mTask;
    private Marker mMarker;
    private String mUserName;
    // private CachedData mCache;
    private static int sColorTaskComplete;

    // LISTENER
    private OnTaskStatusListener mListener;

    public SubtaskView(Context context, OnTaskStatusListener listener) {
        super(context);

        initialization();
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.view_task_new, this);

        mSettings = LTSettings.getInstance(getContext());
        mUserName = mSettings.getUserName();

        if (sColorTaskComplete == 0) {
            sColorTaskComplete = getResources().getColor(R.color.gray_task_complete);
        }

        // TASK STATUS, SUBTASKS COUNT, TASK NAME
        mIvStatus = (ImageView) findViewById(R.id.iv_task_status);
        mBvSubtasks = new BadgeView(getContext(), mIvStatus);
        mBvSubtasks.setTextColor(Color.WHITE);
        mBvSubtasks.setBadgeBackgroundColor(BADGE_GREEN_COLOR);
        mBvSubtasks.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
        final int paddingH = getResources().getDimensionPixelSize(R.dimen.univ_padding_small);
        final int paddingB = getResources().getDimensionPixelSize(R.dimen.univ_padding_tiny);
        final int tsBadge = getResources().getDimensionPixelSize(R.dimen.text_size_less);
        mBvSubtasks.setPadding(paddingH, 0, paddingH, paddingB);
        mBvSubtasks.setTextSize(tsBadge);// 15

        mTvTaskName = (TextView) findViewById(R.id.text_name);

        // STATUS LINE
        mIvUser = (ImageView) findViewById(R.id.img_user);
        mTvUser = (TextView) findViewById(R.id.text_user);
        mIvTerm = findViewById(R.id.img_term);
        mTvTerm = (TextView) findViewById(R.id.text_term);
        mIvTermCustomer = findViewById(R.id.img_term_customer);
        mTvTermCustomer = (TextView) findViewById(R.id.text_term_customer);
        mIvAttachedFiles = findViewById(R.id.attached_files);
        mIvTaskComment = (ImageView) findViewById(R.id.img_comments_text);
        mIvMessages = (ImageView) findViewById(R.id.img_messages);

        // TASK LABEL

        mModifiedViews = new TextView[] { mTvUser, mTvTerm, mTvTermCustomer };

        mIvStatus.setOnClickListener(this);
        this.findViewById(R.id.task_info).setOnClickListener(this);
    }

    public void setData(Cursor cursor) {
        mTask = new Task(cursor);

        mIvStatus.setTag(mTask);
        mIvStatus.setVisibility(View.VISIBLE);

        mMarker = null;// mCache.getMarkerById(mTask.getMarkerUid());
        setTaskStatus(mTask);
        setSubtasksCount(mTask);
        final String taskTitle = TextUtils.isEmpty(mTask.getName()) ? "" : mTask.getName().trim();

        if (mUserName.equals(mTask.getCustomer())) {
            // Customer is current user
            if (mUserName.equals(mTask.getPerformer())) {
                mIvUser.setVisibility(View.GONE);
                mTvUser.setVisibility(View.GONE);
            } else {
                mTvUser.setText(mTask.getPerformer());
                mIvUser.setImageResource(R.drawable.tome_task);

                mIvUser.setVisibility(View.VISIBLE);
                mTvUser.setVisibility(View.VISIBLE);
            }

            mIvTermCustomer.setVisibility(View.GONE);
            mTvTermCustomer.setVisibility(View.GONE);
        } else {
            // we are perfomer or other user
            if (mTask.getTermCustomerBegin() != null && mTask.getTermCustomerEnd() != null) {
                setTerm(mTvTermCustomer, false);
                mIvTermCustomer.setVisibility(View.VISIBLE);
                mTvTermCustomer.setVisibility(View.VISIBLE);
            } else {
                mIvTermCustomer.setVisibility(View.GONE);
                mTvTermCustomer.setVisibility(View.GONE);
            }

            mTvUser.setText(mTask.getCustomer());
            if (mUserName.equals(mTask.getPerformer())) {
                mIvUser.setImageResource(R.drawable.fromme_task);
            } else {
                mIvUser.setImageResource(R.drawable.lock_task);
            }

            mIvUser.setVisibility(View.VISIBLE);
            mTvUser.setVisibility(View.VISIBLE);
        }

        if (mTask.getTermBegin() != null && mTask.getTermEnd() != null) {
            setTerm(mTvTerm, true);
            mIvTerm.setVisibility(View.VISIBLE);
            mTvTerm.setVisibility(View.VISIBLE);
        } else {
            mIvTerm.setVisibility(View.GONE);
            mTvTerm.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(mTask.getComment())) {
            mIvTaskComment.setVisibility(View.GONE);
        } else {
            mIvTaskComment.setVisibility(View.VISIBLE);
        }

        if (mTask.getMessagesCount() != null && mTask.getMessagesCount() > 0) {
            mIvMessages.setVisibility(View.VISIBLE);

        } else {
            mIvMessages.setVisibility(View.GONE);
        }

        if (mTask.getLabels() != null && mTask.getLabels().size() > 0) {
            // addLabels(mTask.getLabels()); TODO

        } else {
        }

        if (mSettings.isStrikethruTask() && Utils.TaskUtils.isCompleted(mTask, mUserName)) {
            setCompletedTextColorDefault();

            mTvTaskName.setBackgroundColor(Color.TRANSPARENT);
            mTvTaskName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTvTaskName.setTypeface(null, Typeface.NORMAL);
            mTvTaskName.setText(taskTitle);
        } else {
            mTvTaskName.setPaintFlags(0);
            if (setMarker(mMarker)) {
                mTvTaskName.setText(taskTitle.toUpperCase());
            } else {
                mTvTaskName.setText(taskTitle);
            }

            if (mTask.isReaded()) {
                mTvTaskName.setTypeface(null, Typeface.NORMAL);
                for (TextView text : mModifiedViews) {
                    text.setTypeface(null, Typeface.NORMAL);
                }
            } else {
                mTvTaskName.setTypeface(null, Typeface.BOLD);
                for (TextView text : mModifiedViews) {
                    text.setTypeface(null, Typeface.BOLD);
                }
            }
        }

        final Cursor c = getContext().getContentResolver().query(TaskFileContract.CONTENT_URI, null,
                TaskFileContract.selectionFieldTaskUidAndDeleteObject(mTask.getId().toString(), false), null, null);
        mIvAttachedFiles.setVisibility(c.getCount() > 0 ? View.VISIBLE : View.GONE);
        c.close();
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) {
            return;
        }

        switch (v.getId()) {
        case R.id.iv_task_status:
            mListener.onTaskStatusClick(mTask);
            break;

        default:
            mListener.onTaskViewClick(mTask);
            break;
        }
    }

    /**
     * Set message marker
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param marker
     *            return isUppercase
     */
    @SuppressWarnings("deprecation")
    private boolean setMarker(Marker marker) {
        if (marker == null ) {
            setBackgroundColorDefault();
            setTextColorDefault();
            return false;

        } else {
            if (marker.getBackColor() == null || Marker.NO_COLOR.equals(marker.getBackColor())) {
                setBackgroundColorDefault();
            } else {
                final int backgroundColor = Color.parseColor(marker.getBackColor());
                final StateListDrawable sld = new StateListDrawable();
                sld.addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(Color.TRANSPARENT));
                sld.addState(new int[] { -android.R.attr.state_pressed }, new ColorDrawable(backgroundColor));
                this.setBackgroundDrawable(sld);
            }

            if (marker.getTextColor() == null || Marker.NO_COLOR.equals(marker.getTextColor())) {
                setTextColorDefault();
            } else {
                final int textColor = Color.parseColor(marker.getTextColor());
                mTvTaskName.setTextColor(textColor);
                for (TextView text : mModifiedViews) {
                    text.setTextColor(textColor);
                }
            }

            return marker.isUppercase();
        }
    }

    private void setTextColorDefault() {
        if (mSettings.isThemeDark()) {
            mTvTaskName.setTextColor(Color.WHITE);
            for (TextView text : mModifiedViews) {
                text.setTextColor(Color.WHITE);
            }
        } else {
            mTvTaskName.setTextColor(Color.BLACK);
            for (TextView text : mModifiedViews) {
                text.setTextColor(Color.BLACK);
            }
        }
    }

    private void setBackgroundColorDefault() {
        this.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setCompletedTextColorDefault() {
        mTvTaskName.setTextColor(sColorTaskComplete);
        for (TextView text : mModifiedViews) {
            text.setTextColor(sColorTaskComplete);
        }
    }

    // set badge view properties
    private void setSubtasksCount(Task task) {
        boolean brown = task.getSubTasksCountNotRead() > 0;
        int count = task.getSubTasksCountNotMade();
        if (!mSettings.isMakeTaskHide()) {
            brown = brown || task.getSubTasksSizeNotMadeAndNotRead() > 0;
            count = task.getSubTasksCount();
        }

        mBvSubtasks.setBadgeBackgroundColor(brown ? BADGE_BROWN_COLOR : BADGE_GREEN_COLOR);

        if (count > 0) {
            mBvSubtasks.setText(String.valueOf(count < 999 ? count : 999));
            mBvSubtasks.show();
        } else {
            mBvSubtasks.hide();
        }
    }

    private void setTaskStatus(Task task) {
        if (mSettings.isThemeDark() && (mMarker == null || mMarker.getBackColor() == null)) {
            mIvStatus.setImageResource(task.getStatusType().getResIdWhite());
        } else {
            mIvStatus.setImageResource(task.getStatusType().getResId());
        }
    }

    private void setTerm(TextView textTerm, boolean isPerformer) {
        final String text = Utils.taskTermFormatter(getContext(), mTask, isPerformer);
        textTerm.setText(text);
    }

    public void setAdapter(TaskAdapter adapter) {
        mIvStatus.setOnClickListener(adapter);
    }

    public void setCustomListener(OnTaskStatusListener listener) {
        mListener = listener;
    }
}