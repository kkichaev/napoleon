package com.ashberrysoft.leadertask.views;

import java.util.List;
import java.util.Locale;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.adapters.TaskAdapter;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.cache.MarkersCacheHolder;
import com.ashberrysoft.leadertask.content_providers.LeaderTaskProviderMetaData.TaskFileContract;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Отображение задачи
 * 
 * @author Tetiana Diachuk (diacht@gmail.com)
 * 
 */
public class TaskView extends LinearLayout implements IDataView<Task> {

    private ImageView mImgStatus, mImgClient, mImgTerm;
    private ImageView mImgComments, mImgCommentsText;
    private TextView mTextName;
    private TextView mTextTerm;
    private TextView mTextClient, mTextComments;
    private TextView mTextTermCustomer;
    private LinearLayout mLayoutUser, mLayoutTermClient, mLayoutTerm, mLayoutComments;
    private RelativeLayout mMainLayout;
    private PredicateLayout mLayoutLabel;
    private LTSettings mSettings;
    private Task mTask;
    private BadgeView mBadgeView;
    private boolean mIsUppercase;
    private TextView mModifiedViews[];
    private Marker mMarker;
    private String mUserName;
    private MarkersCacheHolder mCache;
    private View mFrame;
    private static int sColorTaskComplete;
    private View mAttachedFiles;

    public TaskView(Context context, String userName) {
        super(context);
        // mCache = CachedData.getInstance(getContext());

        mUserName = userName;

        inflate(context, R.layout.view_task, this);

        mAttachedFiles = findViewById(R.id.attached_files);

        mSettings = ((LTApplication) context.getApplicationContext()).getSettings();

        if (sColorTaskComplete == 0) {
            sColorTaskComplete = getResources().getColor(R.color.gray_task_complete);
        }

        mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mImgStatus = (ImageView) findViewById(R.id.img_status);
        mBadgeView = new BadgeView(getContext(), mImgStatus);
        mBadgeView.setTextColor(Color.WHITE);
        mBadgeView.setBadgeBackgroundColor(0x88008800);
        mBadgeView.setTextSize(15);
        mBadgeView.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
        final int paddingHorizontal = Utils.convertDipToPixels(getContext(), 5);
        final int paddingBottom = Utils.convertDipToPixels(getContext(), 2);
        mBadgeView.setPadding(paddingHorizontal, 0, paddingHorizontal, paddingBottom);

        mImgClient = (ImageView) findViewById(R.id.img_user);
        mImgTerm = (ImageView) findViewById(R.id.img_term);
        mImgComments = (ImageView) findViewById(R.id.img_comments);
        mImgCommentsText = (ImageView) findViewById(R.id.img_comments_text);

        mTextTermCustomer = (TextView) findViewById(R.id.text_term_client);
        mTextTerm = (TextView) findViewById(R.id.text_term);
        mTextClient = (TextView) findViewById(R.id.text_user);
        mTextComments = (TextView) findViewById(R.id.text_comments);
        mTextName = (TextView) findViewById(R.id.text_name);

        mModifiedViews = new TextView[] { mTextTermCustomer, mTextTerm, mTextClient, mTextComments };// , mTextName };

        mLayoutUser = (LinearLayout) findViewById(R.id.user);
        mLayoutTermClient = (LinearLayout) findViewById(R.id.term_client);
        mLayoutTerm = (LinearLayout) findViewById(R.id.term);
        mLayoutLabel = (PredicateLayout) findViewById(R.id.label);
        mLayoutComments = (LinearLayout) findViewById(R.id.comments);
        mFrame = findViewById(R.id.frame);
    }

    @Override
    public void setData(Task data) {
        mTask = data;
        mFrame.setTag(mTask);
        mIsUppercase = false;

        mMarker = mCache.findData(mTask.getMarkerUid());
        setLogo(mTask);

        showAll();
        mLayoutLabel.removeAllViews();

        setBadgeView(mTask);
        String taskTitle;
        if (mTask.getName() == null || mTask.getName().isEmpty()) {
            taskTitle = "";
        } else {
            taskTitle = mTask.getName().trim();
        }

        if (mTask.getCustomer().equals(mUserName)) {
            // Customer is current user
            mLayoutTermClient.setVisibility(View.GONE);
            if (mTask.getPerformer().equals(mUserName)) {
                mImgClient.setVisibility(View.GONE);
                mTextClient.setVisibility(View.GONE);
            } else {
                if (mIsUppercase) {
                    String text = " " + mTask.getPerformer();
                    mTextClient.setText(text.toUpperCase());
                } else {
                    mTextClient.setText(" " + mTask.getPerformer());
                }
                mImgClient.setImageResource(R.drawable.tome_task);
            }
        } else {
            // we are perfomer or other user

            // show Customer term
            if ((mTask.getTermCustomerBegin() != null) && (mTask.getTermCustomerEnd() != null)) {
                mLayoutTermClient.setVisibility(View.VISIBLE);
                setTerm(mTextTermCustomer, false);
            } else {
                mLayoutTermClient.setVisibility(View.GONE);
            }

            if (mIsUppercase) {
                String text = " " + mTask.getCustomer();
                mTextClient.setText(text.toUpperCase());
            } else {
                mTextClient.setText(" " + mTask.getCustomer());
            }

            if (mTask.getPerformer().equals(mUserName)) {
                // we are performer
                mImgClient.setImageResource(R.drawable.fromme_task);
            } else {
                mImgClient.setImageResource(R.drawable.lock_task);
            }
        }

        if ((mTask.getTermBegin() != null) && (mTask.getTermEnd() != null)) {
            setTerm(mTextTerm, true);
            mImgTerm.setImageResource(R.drawable.term_orange_tiny);
        } else {
            mLayoutTerm.setVisibility(View.GONE);
        }

        if ((mTask.getComment() != null) && (!(mTask.getComment().equals("")))) {
            mImgCommentsText.setImageResource(R.drawable.comment_task);
        } else {
            if (!((mTask.getMessagesCount() != null) && (mTask.getMessagesCount() > 0))) {
                mLayoutComments.setVisibility(View.GONE);
            } else
                mImgCommentsText.setVisibility(View.GONE);
        }

        if (mTask.getMessagesCount() != null && mTask.getMessagesCount() > 0) {
            mTextComments.setText(" " + mTask.getMessagesCount());
            mImgComments.setImageResource(R.drawable.message_task);
        } else {
            mImgComments.setVisibility(View.GONE);
            mTextComments.setVisibility(View.INVISIBLE);
        }

        if ((mTask.getLabels() != null) && (mTask.getLabels().size() > 0)) {
            // addLabels(mTask.getLabels());
        } else {
            mLayoutLabel.setVisibility(View.GONE);
        }

        if (Utils.TaskUtils.isCompleted(mTask, mUserName)) {
            mTextName.setBackgroundColor(Color.TRANSPARENT);
            mTextName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            mTextName.setTextColor(sColorTaskComplete);
            mTextName.setTypeface(Typeface.DEFAULT);
            mTextName.setText(taskTitle);
        } else {
            if (mIsUppercase) {
                mTextName.setText(taskTitle.toUpperCase());
            } else {
                mTextName.setText(taskTitle);
            }

            mTextName.setTypeface(mTask.isReaded() ? Typeface.DEFAULT : Typeface.DEFAULT_BOLD);

            if (mMarker != null) {
                if (mMarker.getTextColor() == null || Marker.NO_COLOR.equals(mMarker.getTextColor())) {
                    mTextName.setTextColor(mSettings.isThemeDark() ? Color.WHITE : Color.BLACK);
                } else {
                    final int color = Color.parseColor(mMarker.getTextColor());
                    mTextName.setTextColor(color);
                }
            }

            setMarker(mMarker);
        }

        // Unread task will be bold
        if (mTask.isReaded()) {
            for (TextView text : mModifiedViews) {
                text.setTypeface(Typeface.DEFAULT);
            }
        } else {
            for (TextView text : mModifiedViews) {
                text.setTypeface(Typeface.DEFAULT_BOLD);
            }
        }

        final Cursor c = getContext().getContentResolver().query(TaskFileContract.CONTENT_URI, null,
                TaskFileContract.selectionFieldTaskUid(mTask.getId().toString()), null, null);
        mAttachedFiles.setVisibility(c.getCount() > 0 ? View.VISIBLE : View.GONE);
        c.close();
    }

    private void showAll() {
        mLayoutUser.setVisibility(View.VISIBLE);
        mImgClient.setVisibility(View.VISIBLE);
        mTextClient.setVisibility(View.VISIBLE);
        mLayoutTermClient.setVisibility(View.VISIBLE);
        mLayoutTerm.setVisibility(View.VISIBLE);
        mLayoutLabel.setVisibility(View.VISIBLE);
        mLayoutComments.setVisibility(View.VISIBLE);
        mImgCommentsText.setVisibility(View.VISIBLE);
        mImgComments.setVisibility(View.VISIBLE);
        mTextComments.setVisibility(View.VISIBLE);
        mMainLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Set message marker
     * 
     * @author Vladimir Shcryabets <vshcryabets@gmail.com>
     * @param marker
     */
    @SuppressWarnings("deprecation")
    private void setMarker(Marker marker) {
        if (marker != null) {
            if (marker.getBackColor() == null || Marker.NO_COLOR.equals(marker.getBackColor())) {
                setBackgroundColorDefault();
            } else {
                final int backgroundColor = Color.parseColor(marker.getBackColor());
                StateListDrawable sld = new StateListDrawable();
                sld.addState(new int[] { android.R.attr.state_pressed }, new ColorDrawable(Color.TRANSPARENT));
                sld.addState(new int[] { -android.R.attr.state_pressed }, new ColorDrawable(backgroundColor));
                setBackgroundDrawable(sld);
            }

            if (marker.getTextColor() == null || Marker.NO_COLOR.equals(marker.getTextColor())) {
                setTextColorDefault();
            } else {
                final int textColor = Color.parseColor(marker.getTextColor());
                for (TextView text : mModifiedViews) {
                    text.setTextColor(textColor);
                }
            }

            mIsUppercase = marker.isUppercase();
        } else {
            mIsUppercase = false;
            setBackgroundColorDefault();
            setTextColorDefault();
        }
    }

    private void setTextColorDefault() {
        if (mSettings.isThemeDark()) {
            for (TextView text : mModifiedViews) {
                text.setTextColor(Color.WHITE);
            }
        } else {
            for (TextView text : mModifiedViews) {
                text.setTextColor(Color.BLACK);
            }
        }
    }

    private void setBackgroundColorDefault() {
        setBackgroundColor(Color.TRANSPARENT);
    }

    // set badge view properties
    private void setBadgeView(Task data) {
        if (!mSettings.isMakeTaskHide()) {
            /*
             * if task has not read subtasks then badge has brown color otherwise green color
             */
            if (data.getSubTasksCountNotRead() > 0) {
                // brown color
                mBadgeView.setBadgeBackgroundColor(Color.argb(204, 147, 92, 11));
            } else
                // green color
                mBadgeView.setBadgeBackgroundColor(0x88008800);
        } else {
            /*
             * if task has not made and not read subtasks then badge has brown color otherwise green color
             */
            if (data.getSubTasksSizeNotMadeAndNotRead() > 0) {
                // brown color
                mBadgeView.setBadgeBackgroundColor(Color.argb(204, 147, 92, 11));
            } else
                // green color
                mBadgeView.setBadgeBackgroundColor(0x88008800);
        }

        if (mSettings.isMakeTaskHide()) {
            if ((data.getSubTasksCountNotMade() > 0)) {
                if (data.getSubTasksCountNotMade() < 999) {
                    mBadgeView.setText(String.valueOf(data.getSubTasksCountNotMade()));
                } else {
                    mBadgeView.setText(String.valueOf(999));
                }

                mBadgeView.show();
            } else {
                mBadgeView.hide();
            }
        } else {
            if ((data.getSubTasksCount() > 0)) {
                if (data.getSubTasksCount() < 999) {
                    mBadgeView.setText(String.valueOf(data.getSubTasksCount()));
                } else {
                    mBadgeView.setText(String.valueOf(999));
                }

                mBadgeView.show();
            } else {
                mBadgeView.hide();
            }
        }
    }

    private void setLogo(Task data) {
        int resId = 0;
        if (mSettings.isThemeDark() && (mMarker == null || mMarker.getBackColor() == null)) {
            switch (data.getStatus()) {
            case 0:
                resId = R.drawable.status0_white;
                break;
            case 1:
                resId = R.drawable.status1_white;
                break;
            case 3:
                resId = R.drawable.status3_white;
                break;
            case 4:
                resId = R.drawable.status4_white;
                break;
            case 5:
                resId = R.drawable.status5_white;
                break;
            case 6:
                resId = R.drawable.status6_white;
                break;
            case 7:
                resId = R.drawable.status7_white;
                break;
            case 8:
                resId = R.drawable.status8_white;
                break;
            case 9:
                resId = R.drawable.status9_white;
                break;
            default:
                break;
            }
        } else {
            switch (data.getStatus()) {
            case 0:
                resId = R.drawable.status0;
                break;
            case 1:
                resId = R.drawable.status1;
                break;
            case 3:
                resId = R.drawable.status3;
                break;
            case 4:
                resId = R.drawable.status4;
                break;
            case 5:
                resId = R.drawable.status5;
                break;
            case 6:
                resId = R.drawable.status6;
                break;
            case 7:
                resId = R.drawable.status7;
                break;
            case 8:
                resId = R.drawable.status8;
                break;
            case 9:
                resId = R.drawable.status9;
                break;

            default:
                break;
            }
        }
        mImgStatus.setImageResource(resId);
    }

    private void setTerm(TextView textTerm, boolean isPerformer) {
        final String text = Utils.taskTermFormatter(getContext(), mTask, isPerformer);
        if (mIsUppercase) {
            textTerm.setText(text.toUpperCase(Locale.getDefault()));
        } else {
            textTerm.setText(text);
        }
    }

    @Override
    public Task getData() {
        return mTask;
    }

    public void setAdapter(TaskAdapter adapter) {
        mFrame.setOnClickListener(adapter);
    }

    public ImageView getImageView() {
        return mImgStatus;
    }

    public RelativeLayout getRootView() {
        return mMainLayout;
    }

}
