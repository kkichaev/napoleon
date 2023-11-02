package com.ashberrysoft.leadertask.views;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.cache.CachedEmployee;
import com.ashberrysoft.leadertask.cache.MarkersCacheHolder;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.enums.TaskStatusBehavior;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Отображение задачи
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class TaskViewNew extends LinearLayout implements IDataView<Task> {

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

    // TASK LABEL

    private TextView[] mModifiedViews;

    // VALUE's
    private LTSettings mSettings;
    private MarkersCacheHolder mCachedMarkerData;
    private CachedEmployee mCachedEmployee;
    private Task mTask;
    private Marker mMarker;
    private String mUserName;
    private int mColorTaskComplete;
    private StringBuilder mStringBuilder;
    private Calendar mCalendar;

    public TaskViewNew(Context context, OnClickListener listener) {
        super(context);

        initialization();
        setCustomListener(listener);
    }

    private void initialization() {
        inflate(getContext(), R.layout.view_task_new, this);
        this.setOrientation(HORIZONTAL);

        mCalendar = Calendar.getInstance();

        mCachedMarkerData = MarkersCacheHolder.getInstance(getContext());

        mCachedEmployee = CachedEmployee.getInstance(getContext());

        mSettings = LTSettings.getInstance(getContext());
        mUserName = mSettings.getUserName();

        mColorTaskComplete = getResources().getColor(R.color.gray_task_complete);
        mStringBuilder = new StringBuilder();

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
        // mBvSubtasks.setTextSize(tsBadge);// 15
        mBvSubtasks.setTextSize(TypedValue.COMPLEX_UNIT_PX, tsBadge);

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
    }

    @Override
    public void setData(Task task) {
        mTask = task;
        final boolean taskComplete = Utils.TaskUtils.isCompleted(mTask, mUserName);

        mIvStatus.setTag(mTask);

        mMarker = mCachedMarkerData.findData(mTask.getMarkerUid());
        setTaskStatus(mTask);
        setSubtasksCount(mTask);
        final String taskTitle = TextUtils.isEmpty(mTask.getName()) ? SharedStrings.EMPTY : mTask.getName();

        final boolean termBeginEndNotNull = mTask.getTermBegin() != null && mTask.getTermEnd() != null;

        if (termBeginEndNotNull) {
            setTerm(mTvTerm, true, true);
            mIvTerm.setVisibility(View.VISIBLE);
            mTvTerm.setVisibility(View.VISIBLE);

            mIvTermCustomer.setVisibility(View.GONE);
            mTvTermCustomer.setVisibility(View.GONE);

            setWhatUserAndTerm(false, termBeginEndNotNull);

        } else {
            mIvTerm.setVisibility(View.GONE);
            mTvTerm.setVisibility(View.GONE);

            setWhatUserAndTerm(true, termBeginEndNotNull);
        }

        mIvTaskComment.setVisibility(TextUtils.isEmpty(mTask.getComment()) ? View.GONE : View.VISIBLE);
        mIvMessages.setVisibility(mTask.getMessagesCount() != null && mTask.getMessagesCount() > 0 ? View.VISIBLE : View.GONE);

        if (mSettings.isStrikethruTask() && taskComplete) {
            setCompletedTextColorDefault();

            mTvTaskName.setBackgroundColor(Color.TRANSPARENT);
            mTvTaskName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

            setTypeFace(mTvTaskName, true);
            for (TextView text : mModifiedViews) {
                setTypeFace(text, true);
            }

            mTvTaskName.setText(taskTitle);

        } else {
            mTvTaskName.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            if (setMarker(mMarker)) {
                mTvTaskName.setText(taskTitle.toUpperCase());
            } else {
                mTvTaskName.setText(taskTitle);
            }

            setTypeFace(mTvTaskName, mTask.isReaded());
            for (TextView text : mModifiedViews) {
                setTypeFace(text, mTask.isReaded());
            }
        }

        mIvAttachedFiles.setVisibility(mTask.isHasFiles() ? View.VISIBLE : View.GONE);
    }

    private void setWhatUserAndTerm(boolean setTerm, boolean termBeginEndNotNull) {
        if (mUserName.equals(mTask.getCustomer())) {
            // Customer is current user
            if (mUserName.equals(mTask.getPerformer())) {
                mIvUser.setVisibility(View.GONE);
                mTvUser.setVisibility(View.GONE);

            } else {
                mTvUser.setText(mCachedEmployee.getName(mTask.getPerformer()));
                mIvUser.setImageResource(R.drawable.tome_task);

                mIvUser.setVisibility(View.VISIBLE);
                mTvUser.setVisibility(View.VISIBLE);
            }

            if (setTerm) {
                mIvTermCustomer.setVisibility(View.GONE);
                mTvTermCustomer.setVisibility(View.GONE);
            }

        } else {
            // we are perfomer or other user
            if (setTerm) {
                if (mTask.getTermCustomerBegin() != null && mTask.getTermCustomerEnd() != null) {
                    setTerm(mTvTermCustomer, false, !termBeginEndNotNull);
                    mIvTermCustomer.setVisibility(View.VISIBLE);
                    mTvTermCustomer.setVisibility(View.VISIBLE);

                } else {
                    mIvTermCustomer.setVisibility(View.GONE);
                    mTvTermCustomer.setVisibility(View.GONE);
                }
            }

            final String customer = mCachedEmployee.getName(mTask.getCustomer());
            if (mUserName.equals(mTask.getPerformer())) {
                mTvUser.setText(customer);
                mIvUser.setImageResource(R.drawable.fromme_task);

            } else {
                Utils.clearStringBuilder(mStringBuilder);
                mStringBuilder.append(customer);

                if (!mTask.getCustomer().equals(mTask.getPerformer())) {
                    mStringBuilder.append(SharedStrings.ARROW_RIGHT);
                    mStringBuilder.append(mCachedEmployee.getName(mTask.getPerformer()));
                }

                mTvUser.setText(mStringBuilder);
                mIvUser.setImageResource(R.drawable.lock_task);
            }

            mIvUser.setVisibility(View.VISIBLE);
            mTvUser.setVisibility(View.VISIBLE);
        }
    }

    private void setTypeFace(TextView tv, boolean normal) {
        tv.setTypeface(null, normal ? Typeface.NORMAL : Typeface.BOLD);
    }

    private boolean showExpired(boolean taskComplete, boolean termBeginEndNotNull) {// TODO
        if (!taskComplete) {
            TimeHelper.roundCalendar(mCalendar, true);

            if (termBeginEndNotNull) {
                return mTask.getTermEnd().getTime() < mCalendar.getTimeInMillis();

            } else if (mUserName.equals(mTask.getPerformer()) && mTask.getTermEndCustomer() != null) {
                return mTask.getTermEndCustomer().getTime() < mCalendar.getTimeInMillis();
            }
        }
        return false;
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
        if (marker == null) {// || Marker.DEFAULT_MARKER_UUID.equals(marker.getIdTask())) {
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
        mTvTaskName.setTextColor(mColorTaskComplete);
        for (TextView text : mModifiedViews) {
            text.setTextColor(mColorTaskComplete);
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
        final boolean whiteStatus = mSettings.isThemeDark() && (mMarker == null || mMarker.getBackColor() == null);
        final TaskStatus status = task.getStatusType();

        if (mTask.getSeriesType() == SeriesType.NONE.ordinal()) {
            mIvStatus.setImageResource(whiteStatus ? status.getResIdWhite() : status.getResId());
        } else {
            mIvStatus.setImageResource(whiteStatus ? status.getSeriesWhiteResId() : status.getSeriesResId());
        }
    }

    private void setTerm(TextView textTerm, boolean isPerformer, boolean appendSeriesString) {
        Utils.clearStringBuilder(mStringBuilder);

        mStringBuilder.append(Utils.taskTermFormatter(getContext(), mTask, isPerformer));
        if (appendSeriesString) {
            Task.appendSeriesString(getContext(), mStringBuilder, mTask);
        }

        textTerm.setText(mStringBuilder.toString());
    }

    @Override
    public Task getData() {
        return mTask;
    }

    public void setCustomListener(OnClickListener listener) {
        if (mSettings.getStatusBehavior() != TaskStatusBehavior.NONE) {
            mIvStatus.setOnClickListener(listener);
        }
    }
}