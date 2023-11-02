package com.ashberrysoft.leadertask.views;

import java.sql.SQLException;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Email;
import com.ashberrysoft.leadertask.domains.ordinary.FilterNumberTask;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuHeader;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuTreeDataContainer;
import com.ashberrysoft.leadertask.service.ServiceConstants;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dao.ITreeData;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.views.IDataView;

/**
 * Отображение одного элемента фильтра
 * 
 * @author A.Menyaylo (anton.menyaylo@gmail.com)
 * @author Tetiana Diachuk (diacht@gmail.com)
 */

public class SlidingMenuListItem extends RelativeLayout implements IDataView<ITreePureNode>, OnClickListener {

    private static RelativeLayout.LayoutParams RELATIVE_LP = getRelativeLayout_LayoutParams();

    // VIEW's
    protected ImageView mIcon;
    protected TextView mTitle;
    protected ImageView mDropDownView;
    protected View mDivider;

    // VALUE's
    protected ITreePureNode mData;
    private int mIconResourceId;
    private BadgeView mBadgeView;
    private int mMode;
    private static Integer sDividerColor;

    public SlidingMenuListItem(Context context) {
        super(context);
        initialization();
    }

    public SlidingMenuListItem(Context context, int iconResourceId, int mode) {
        super(context);

        mIconResourceId = iconResourceId;
        mMode = mode;
        initialization();
    }

    public SlidingMenuListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialization();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SlidingMenuListItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialization();
    }

    protected void inflateView() {
        inflate(getContext(), R.layout.list_item_sliding_menu, this);
    }

    protected void initialization() {
        inflateView();

        if (sDividerColor == null) {
            sDividerColor = getResources().getColor(R.color.divider_gray);
        }

        mDivider = new View(getContext());
        mDivider.setBackgroundColor(sDividerColor);
        this.addView(mDivider);

        mIcon = (ImageView) findViewById(R.id.icon);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mDropDownView = (ImageView) findViewById(R.id.img_drop_down);
        mDropDownView.setOnClickListener(this);

        if (mIconResourceId != 0) {
            mIcon.setImageResource(mIconResourceId);
        }

        final int paddingHorizontal = Utils.convertDipToPixels(getContext(), 5);
        final int paddingBottom = Utils.convertDipToPixels(getContext(), 2);
        final int tsBadge = getResources().getDimensionPixelSize(R.dimen.text_size_less);

        mBadgeView = new BadgeView(getContext(), mIcon);
        mBadgeView.setTextColor(Color.WHITE);
        mBadgeView.setBadgeBackgroundColor(0x88008800);
        // mBadgeView.setTextSize(tsBadge);// 12
        mBadgeView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tsBadge);
        mBadgeView.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
        mBadgeView.setPadding(paddingHorizontal, 0, paddingHorizontal, paddingBottom);
    }

    @Override
    public void setData(ITreePureNode data) {
        mData = data;

        final boolean isHeader = data.getClass().equals(SlidingMenuHeader.class);
        final SlidingMenuTreeDataContainer container = (SlidingMenuTreeDataContainer) data;

        if (FilterNumberTask.RECORD_TODAY.equals(container.getName())) {
            mTitle.setText(R.string.task_today);
        } else if (FilterNumberTask.RECORD_INCOME.equals(container.getName())) {
            mTitle.setText(R.string.sm_input);
        } else {
            // TODO check this out
            if (data.getClass().equals(Email.class)) {
                final Email email = (Email) data;
                final String title = email.getTitle() == null ? email.getName() : email.getTitle();

                mTitle.setText(title);
                mTitle.setTextColor(Color.WHITE);
            } else {
                final String name = container.getName();
                if (isHeader) {
                    mTitle.setText(name.toUpperCase(Locale.getDefault()));
                    mTitle.setTextColor(sDividerColor);
                } else {
                    mTitle.setText(name);
                    mTitle.setTextColor(Color.WHITE);
                }
            }
        }

        if (!isHeader && mData.isExpandable()) {
            if (mData.isExpandable() && ((ITreeData<?>) mData).isExpanded()) {
                mDropDownView.setImageResource(R.drawable.arrow_down_white_small);
            } else {
                mDropDownView.setImageResource(R.drawable.arrow_left_white_small);
            }

            mDropDownView.setVisibility(VISIBLE);
        } else {
            mDropDownView.setVisibility(GONE);
        }

        final int univPadding = getResources().getDimensionPixelSize(R.dimen.univ_padding);
        final int paddingLeft = univPadding * container.getIndent();
        setPadding(paddingLeft, 0, 0, 0);

        setDivider(isHeader, univPadding);

        try {
            showBadge(container.getFilterId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDivider(boolean bold, int univPadding) {
        final int height = getResources().getDimensionPixelSize(bold ? R.dimen.divider_big : R.dimen.divider_small);
        RELATIVE_LP.height = height;
        RELATIVE_LP.setMargins(bold ? 0 : univPadding, 0, 0, 0);

        mDivider.setLayoutParams(RELATIVE_LP);
    }

    private void showBadge(String filterId) throws SQLException {
        mBadgeView.hide();

        LTSettings settings = ((LTApplication) (getContext().getApplicationContext())).getSettings();
        FilterNumberTask result = DbHelper.getInstance(getContext()).getFilterNumberTask(filterId, mMode);
        if (result == null) {
            return;
        }

        int subTasksCount, notReadCount;
        if (settings.isMakeTaskHide()) {
            subTasksCount = result.getTaskNotDone();
            notReadCount = result.getTaskNotReadForNotDone();
        } else {
            subTasksCount = result.getTaskAll();
            notReadCount = result.getTaskNotReadForAll();
        }

        if (subTasksCount > 0) {
            // set task count for badge view
            if (subTasksCount < 1000) {
                final String text = Integer.toString(subTasksCount);
                mBadgeView.setText(text);
            } else {
                mBadgeView.setText(String.valueOf(999));
            }
            /*
             * set badge view color depending on task count, that not read > 0 - brown color == 0 - green color (by
             * default)
             */
            if (notReadCount > 0) {
                mBadgeView.setBadgeBackgroundColor(Color.argb(204, 147, 92, 11));
            } else {
                mBadgeView.setBadgeBackgroundColor(0x88008800);
            }

            mBadgeView.toggle();
        }
    }

    @Override
    public ITreePureNode getData() {
        return mData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_drop_down:
            if (mData.isExpandable()) {
                final ITreeData<?> data = (ITreeData<?>) mData;
                final boolean isExpandedNew = !data.isExpanded();

                data.setExpanded(isExpandedNew);
                saveExpanded(getContext(), isExpandedNew);
                saveProject(!isExpandedNew);
                saveCategory(!isExpandedNew);

                final Intent intent = new Intent();
                intent.setAction(ServiceConstants.ACTION_NOTIFY_DATASET_CHANGED_SLIDING_MENU);
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
            }
        default:
            break;
        }
    }

    private void saveProject(boolean isExpandedNew) {
        final Class<?> inputClass = mData.getClass();

        if (inputClass.equals(Project.class)) {
            final Project project = (Project) mData;
            project.setCollapsed(isExpandedNew);
            DbHelper.getInstance(getContext()).updateProject(project);
        }
    }

    private void saveCategory(boolean isExpandedNew) {
        final Class<?> inputClass = mData.getClass();

        if (inputClass.equals(Category.class)) {
            final Category category = (Category) mData;
            DbHelper.getInstance(getContext()).setCategoryCollapsed(category, isExpandedNew);
        }
    }

    private void saveExpanded(Context context, boolean value) {
        final LTSettings settings = ((LTApplication) context.getApplicationContext()).getSettings();

        if (mTitle.getText().equals(context.getString(R.string.sm_instruct_i))) {
            settings.setIsSlidingInstructIExpande(value);
        }

        else if (mTitle.getText().equals(context.getString(R.string.sm_instruct_me))) {
            settings.setIsSlidingInstructMyExpande(value);
        }

        else if (mTitle.getText().equals(context.getString(R.string.sm_projects))) {
            settings.setIsSlidingProjectExpanded(value);
        }

        else if (mTitle.getText().equals(context.getString(R.string.sm_available_me))) {
            settings.setIsSlidingAvalaibleProjectExpanded(value);
        }

        else if (mTitle.getText().equals(context.getString(R.string.sm_categories))) {
            settings.setIsSlidingCategoryExpanded(value);
        }
    }

    private static RelativeLayout.LayoutParams getRelativeLayout_LayoutParams() {
        final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.BELOW, R.id.sliding_menu_item);

        return lp;
    }
}