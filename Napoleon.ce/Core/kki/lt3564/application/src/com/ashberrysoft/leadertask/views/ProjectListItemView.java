package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.views.IDataView;

import java.util.UUID;

/**
 * Класс, предназначенный для формирования внешнего вида элемента слайдинг-меню
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * @author Vladimir Shcryabets <vshcryabets@gmail.com>
 * 
 */
public class ProjectListItemView extends LinearLayout implements IDataView<ITreePureNode>, View.OnClickListener,
        OnCheckedChangeListener {

    public interface OnProjectItemViewListener {
        public void onProjectClick(Project project, boolean isChecked);

        public void onProjectOpen(Project project, boolean isCollapsed);
    }

    // VIEW's
    private ImageView mDropDown;
    private ImageView mIcon;
    private TextView mTitle;
    private CheckBox mCheckBox;
    private Context mContext;

    // VALUE's
    private Project mData;
    private boolean mIsChecked;

    // LISTENER
    private OnProjectItemViewListener mListener;

    public ProjectListItemView(Context context) {
        super(context);
        initialization();
    }

    public ProjectListItemView(Context context, OnProjectItemViewListener listener) {
        super(context);
        mContext = context;
        initialization();
        setCustomListener(listener);
    }

    protected void initialization() {
        inflate(getContext(), R.layout.list_item_sliding_menu_for_project_dialog, this);
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER_VERTICAL);

        mDropDown = (ImageView) findViewById(R.id.img_drop_down);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mCheckBox = (CheckBox) findViewById(R.id.check_box);
        mIcon = (ImageView) findViewById(R.id.icon);

        mIcon.setOnClickListener(this);
        mDropDown.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mCheckBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void setData(ITreePureNode data) {
        mData = (Project) data;

        final int paddingLeft = Utils.convertDipToPixels(getContext(), 15 * mData.getIndent());
        this.setPadding(paddingLeft, 0, 0, 0);

        if (mData.isExpandable()) {
            mDropDown.setVisibility(VISIBLE);
            mDropDown.setImageResource(mData.isExpanded() ? R.drawable.arrow_down : R.drawable.arrow_right);
        } else {
            mDropDown.setVisibility(INVISIBLE);
        }

        // mDate.getID()       // UUID проекта
        Project project = getProjectByUUID(mData.getId());
        if(project.getCreator().equals(LTSettings.getInstance(mContext).getUserName())) {
            mIcon.setImageResource(project.getSharedUsers() != null ? R.drawable.project_shared : R.drawable.project);
        }
        else {
            mIcon.setImageResource(R.drawable.project_available);
        }
        mTitle.setText(mData.getName());
    }

    public void setChecked(Project currentProject) {
        mIsChecked = currentProject != null && currentProject.getId().equals(mData.getId());
        mCheckBox.setChecked(mIsChecked);
    }

    @Override
    public ITreePureNode getData() {
        return mData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_drop_down:
            if (mListener != null) {
                mListener.onProjectOpen(mData, mData.isCollapsed());
            }
            break;

        case R.id.icon:
        case R.id.txt_title:
            mCheckBox.setChecked(!mCheckBox.isChecked());
        default:
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton bV, boolean isChecked) {
        if (mListener != null) {
            if (mIsChecked) {
                mListener.onProjectClick(isChecked ? mData : null, mIsChecked);
            } else {
                mListener.onProjectClick(isChecked ? mData : null, isChecked);
            }
        }
    }

    public void setCustomListener(OnProjectItemViewListener listener) {
        mListener = listener;
    }

    private Project getProjectByUUID(UUID ProjectUid) {
        try {
            return DbHelper.getInstance(mContext).getProjectByUUId(ProjectUid);
        }
        catch (Exception e){ }
        return null;
    }
}