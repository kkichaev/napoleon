package com.ashberrysoft.leadertask.views;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.cache.CachedEmployee;
import com.ashberrysoft.leadertask.cache.MarkersCacheHolder;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * 
 * @author Tregub Artem tregub.artem@gmail.com
 */

public class EditTaskHeaderView extends LinearLayout//
        implements View.OnClickListener, View.OnLongClickListener {

    private static final String CLASS_PATH = EditTaskHeaderView.class.getName();
    private static final String EXTRA_TITLE = CLASS_PATH + "EXTRA_TITLE";
    private static final String EXTRA_COMMENT = CLASS_PATH + "EXTRA_COMMENT";

    // VIEW's
    private final View mLlStatusTitle;
    private final ImageView mStatus;
    private final EditText mTitle;
    private final TextView mTvTitle;

    private final View mLlComment;
    private final EditText mComment;
    private final TextView mTvComment;

    private final EditTaskFeatureHeaderView mPerformer;
    private final EditTaskFeatureHeaderView mTerm;

    private EditTaskFeatureHeaderView mMarker;
    private EditTaskFeatureHeaderView mProject;
    private EditTaskFeatureHeaderView mCategories;
    private EditTaskFeatureHeaderView mLabels;

    // VALUE's
    private final LTSettings mSettings;
    private final StringBuilder mStringBuilder;

    private boolean mImCustomer;
    private OnClickListener mFragmentClickListener;

    public EditTaskHeaderView(Context context, Task task, DbHelper dbHelper, ListView lv, Bundle b,
            Set<Category> categories) {
        this(context);

        setHeaders(lv, dbHelper, mSettings.getUserName().equals(task.getCustomer()));
        setData(task, dbHelper, b, categories);
    }

    public EditTaskHeaderView(Context context) {
        this(context, null);
    }

    public EditTaskHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSettings = LTSettings.getInstance(getContext());
        mStringBuilder = new StringBuilder();

        mLlStatusTitle = inflate(getContext(), R.layout.view_header_assign_status_title, null);
        mStatus = (ImageView) mLlStatusTitle.findViewById(R.id.status);
        mTitle = (EditText) mLlStatusTitle.findViewById(R.id.title);
        mTvTitle = (TextView) mLlStatusTitle.findViewById(R.id.tv_title);
        mTvTitle.setVisibility(View.GONE);

        mLlComment = inflate(getContext(), R.layout.view_header_assign_comment, null);
        mComment = (EditText) mLlComment.findViewById(R.id.comment);
        mTvComment = (TextView) mLlComment.findViewById(R.id.tv_comment);
        mComment.setVisibility(View.GONE);

        mPerformer = new EditTaskFeatureHeaderView(getContext());
        mPerformer.setId(R.id.performer);
        mPerformer.setHint(R.string.menu_assign);

        mTerm = new EditTaskFeatureHeaderView(getContext());
        mTerm.setId(R.id.term);
        mTerm.setHint(R.string.task_term);

        {
            final int textColor = mSettings.isThemeDark() ? Color.WHITE : Color.BLACK;

            mTitle.setTextColor(textColor);
            mTvTitle.setTextColor(textColor);
            mComment.setTextColor(textColor);
            mTvComment.setTextColor(textColor);

            final int dividerColor = mSettings.isThemeDark() ? Color.WHITE : getResources().getColor(
                    R.color.divider_gray);

            mLlStatusTitle.findViewById(R.id.divider).setBackgroundColor(dividerColor);
            mLlComment.findViewById(R.id.divider).setBackgroundColor(dividerColor);
        }
    }

    public void setHeaders(ListView lv, DbHelper dbHelper, boolean isCustomer) {
        mImCustomer = isCustomer;

        lv.addHeaderView(mLlStatusTitle, null, false);
        lv.addHeaderView(mLlComment);
        lv.addHeaderView(mPerformer, null, false);
        lv.addHeaderView(mTerm, null, false);

        if (MarkersCacheHolder.getInstance(getContext()).hasCustomMarkers()) {
            mMarker = new EditTaskFeatureHeaderView(getContext());
            mMarker.setId(R.id.marker);
            mMarker.setHint(R.string.default_marker);
            mMarker.setImageResource(mSettings.isThemeDark() ? R.drawable.marker_white : R.drawable.marker_black);

            lv.addHeaderView(mMarker, null, false);
        }

        try {
            if (dbHelper.getProjectDao().queryForAll().size() > 0) {
                mProject = new EditTaskFeatureHeaderView(getContext());
                mProject.setId(R.id.project);
                mProject.setHint(R.string.default_project);
                mProject.setImageResource(mSettings.isThemeDark() ? R.drawable.project : R.drawable.project);

                lv.addHeaderView(mProject, null, false);
            }
        } catch (Exception e) {}

        try {
            if (dbHelper.getCategoryDao().queryForAll().size() > 0) {
                mCategories = new EditTaskFeatureHeaderView(getContext());
                mCategories.setId(R.id.categories);
                mCategories.setHint(R.string.task_category);
                mCategories.setImageResource(mSettings.isThemeDark() ? R.drawable.category_white_big
                        : R.drawable.category);

                lv.addHeaderView(mCategories, null, false);
            }
        } catch (Exception e) {}

        if (!mImCustomer) {
            mPerformer.setEnabled(false);
            mProject.setEnabled(false);
        }
    }

    public void setData(Task task, DbHelper dbHelper, Bundle b, Set<Category> categories) {
        setPerformer(task);
        setTerm(task);
        setStatus(task);

        if (b == null) {
            setTitle(task.getName());
            setComment(task.getComment());

        } else {
            onRestoreInstanceState(b);
        }

        setMarker(MarkersCacheHolder.getInstance(getContext()).findData(task.getMarkerUid()));

        try {
            setProject(task.getProjectUid() != null ? dbHelper.getProjectDao().queryForId(task.getProjectUid()) : null);

        } catch (Exception e) {
            setProject(null);
        }

        try {
            if (categories.isEmpty()) {
                categories.addAll(dbHelper.getCategoriesListByTask(task.getId()));
            }

            setCategories(categories);

        } catch (SQLException e) {
            setCategories(null);
        }
    }

    public void setPerformer(Task task) {
        final String performer = task.getPerformer();
        final boolean imCustomer = mSettings.getUserName().equals(task.getCustomer());
        final boolean imPerformer = mSettings.getUserName().equals(performer);

        if (TextUtils.isEmpty(performer) || imCustomer && imPerformer) {
            mPerformer.setText(null);
            mPerformer.setImageResource(R.drawable.user_gray);

        } else {
            final String findName;
            if (imCustomer && !imPerformer) {
                mPerformer.setImageResource(R.drawable.user_green);
                findName = task.getPerformer();

            } else if (!imCustomer && imPerformer) {
                mPerformer.setImageResource(R.drawable.user_red);
                findName = task.getCustomer();

            } else {
                mPerformer.setImageResource(R.drawable.user_lock);
                findName = task.getCustomer();
            }

            mPerformer.setText(CachedEmployee.getInstance(getContext()).getName(findName));
        }
    }

    public void setTerm(Task task) {
        final boolean imPerformer = mSettings.getUserName().equals(task.getPerformer());
        String term = Utils.taskTermFormatter(getContext(), task, imPerformer);

        if (TextUtils.isEmpty(term)) {
            if (imPerformer) {
                term = Utils.taskTermFormatter(getContext(), task, false);
                if (TextUtils.isEmpty(term)) {
                    setTermEmpty();
                } else {
                    setTermDate(term, !imPerformer, task);
                }
            } else {
                setTermEmpty();
            }
        } else {
            setTermDate(term, true, task);
        }
    }

    private void setTermEmpty() {
        mTerm.setImageResource(R.drawable.term_gray_small);
        mTerm.setText(null);
    }

    private void setTermDate(String term, boolean orange, Task task) {
        Utils.clearStringBuilder(mStringBuilder);
        mStringBuilder.append(term);
        Task.appendSeriesString(getContext(), mStringBuilder, task);

        mTerm.setImageResource(orange ? R.drawable.term_orange_small_l : R.drawable.term_red_big);
        mTerm.setText(mStringBuilder.toString());
    }

    public void setStatus(Task task) {
        final TaskStatus status = task.getStatusType();

        if (task.getSeriesType() == SeriesType.NONE.ordinal()) {
            mStatus.setImageResource(mSettings.isThemeDark() ? status.getResIdWhite() : status.getResId());
        } else {
            mStatus.setImageResource(mSettings.isThemeDark() ? status.getSeriesWhiteResId() : status.getSeriesResId());
        }
    }

    public void setTitle(String title) {
        mTitle.setText(title);
        mTvTitle.setText(title);
    }

    public void setComment(String comment) {
        mComment.setText(comment);
        mTvComment.setText(comment);
    }

    public void setMarker(Marker marker) {
        if (mMarker != null) {
            if (marker == null ) {
                mMarker.setText(null);

            } else {
                mMarker.setText(marker.isUppercase() ? marker.getName().toUpperCase() : marker.getName());
            }
        }
    }

    public void setProject(Project project) {
        if (mProject != null) {
            mProject.setText(project != null ? project.getName() : null);
        }
    }

    public void setCategories(Set<Category> categories) {
        if (mCategories != null) {
            Utils.clearStringBuilder(mStringBuilder);

            if (categories != null && categories.size() > 0) {
                boolean first = true;

                for (Category c : categories) {
                    if (first) {
                        first = false;

                    } else {
                        mStringBuilder.append(SharedStrings.COMMA_C);
                    }

                    mStringBuilder.append(c.getName());
                }
            }

            mCategories.setText(mStringBuilder);
        }
    }

    public void setViewsOnClickListener(OnClickListener listener) {
        mFragmentClickListener = listener;

        mStatus.setOnClickListener(this);
        mTvTitle.setOnClickListener(this);
        mTvComment.setOnClickListener(this);
        mPerformer.setOnClickListener(this);
        mTerm.setOnClickListener(this);

        if (mMarker != null) {
            mMarker.setOnClickListener(this);
        }
        if (mProject != null) {
            mProject.setOnClickListener(this);
        }
        if (mCategories != null) {
            mCategories.setOnClickListener(this);
        }
        if (mLabels != null) {
            mLabels.setOnClickListener(this);
        }

        mTvTitle.setOnLongClickListener(this);
        mTvComment.setOnLongClickListener(this);
    }

    public void onSavedInstanceState(Bundle b) {
        b.putSerializable(EXTRA_TITLE, mTitle.getText().toString());
        b.putSerializable(EXTRA_COMMENT, mComment.getText().toString());
    }

    private void onRestoreInstanceState(Bundle b) {
        setTitle(b.getString(EXTRA_TITLE));
        setComment(b.getString(EXTRA_COMMENT));
    }

    public String getTitle() {
        final String title = mTitle.getText().toString();
        return TextUtils.isEmpty(title) ? null : title;
    }

    public String getComment() {
        final String comment = mComment.getText().toString();
        return TextUtils.isEmpty(comment) ? null : comment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.tv_title:
            if (mImCustomer) {
                stopTextView(mTvTitle, mTitle);
                startTextView(mTvComment, mComment);
            }
            break;

        case R.id.tv_comment:
            if (mImCustomer) {
                stopTextView(mTvComment, mComment);
                startTextView(mTvTitle, mTitle);
            }
            break;

        default:
            startTextView(mTvTitle, mTitle);
            startTextView(mTvComment, mComment);
            Utils.hideInput(getContext(), v);
            mFragmentClickListener.onClick(v);
            break;
        }
    }

    private void stopTextView(TextView tv, EditText et) {
        if (tv.getVisibility() != View.GONE) {
            tv.setVisibility(View.GONE);
            et.setVisibility(View.VISIBLE);
            et.requestFocus();
            et.setSelection(et.getText().length());
            Utils.showInput(getContext(), et);
        }

    }

    private void startTextView(TextView tv, EditText et) {
        if (et.getVisibility() != View.GONE) {
            tv.setText(et.getText().toString());

            et.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (!mImCustomer) {
            return true;
        }

        switch (v.getId()) {
        case R.id.tv_title:
            stopTextView(mTvTitle, mTitle);
            startTextView(mTvComment, mComment);
            return true;

        case R.id.tv_comment:
            stopTextView(mTvComment, mComment);
            startTextView(mTvTitle, mTitle);
            return true;

        default:
            return false;
        }
    }

    public EditText getEditTextTitle() {
        return mTitle;
    }
}