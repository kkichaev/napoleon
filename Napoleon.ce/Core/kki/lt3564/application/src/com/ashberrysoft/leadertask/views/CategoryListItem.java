package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.SlidingMenuTreeDataContainer;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.dao.ITreePureNode;
import com.v2soft.AndLib.ui.views.IDataView;


public class CategoryListItem extends LinearLayout implements IDataView<ITreePureNode>, OnClickListener,
        OnCheckedChangeListener {

    // TODO Bug #3519 added interface and everething that wis it
    public interface OnCategoryListItemListener {
        public void onCategoryChecked(Category category, boolean isChecked);

        public void onCategoryOpen(Category category);
    }

    // VIEW's
    private ImageView mIcon;
    private RelativeLayout mIconContainer;
    private TextView mTitle;
    private ImageView mDropDownView;
    private CheckBox mCheckbox;
    private LTApplication mApp;

    // VALUE's
    protected Category mCategory;

    // LISTENER
    private OnCategoryListItemListener mListener;

    public CategoryListItem(Context context) {
        super(context);
        initialization(context);

    }

    public CategoryListItem(Context context, OnCategoryListItemListener listener) {
        super(context);

        setCustomListener(listener);
        initialization(context);
    }

    private void initialization(Context context) {
        inflate(getContext(), R.layout.list_item_sliding_menu_for_category_dialog, this);
        mApp = (LTApplication) context.getApplicationContext();
        mDropDownView = (ImageView) findViewById(R.id.img_drop_down);
        mIconContainer = (RelativeLayout) findViewById(R.id.icon_category_container);
        mIcon = (ImageView) findViewById(R.id.icon_category);
        mTitle = (TextView) findViewById(R.id.txt_title);
        mCheckbox = (CheckBox) findViewById(R.id.checkbox);

        mDropDownView.setOnClickListener(this);
        mIconContainer.setOnClickListener(this);
        mTitle.setOnClickListener(this);
        mCheckbox.setOnClickListener(this);
        mCheckbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void setData(ITreePureNode data) {
        mCategory = (Category) data;
        if(mCategory.getColor() != null && !mCategory.getColor().equals(Category.NO_COLOR) ) {
            mIcon.setImageBitmap(Utils.getCategoryDrawable(mApp, mCategory.getColor()));
        }
        else {
            //mIcon.setImageResource(R.drawable.category);
            mIcon.setImageBitmap(Utils.getCategoryDrawable(mApp, null));
        }
        //
        final SlidingMenuTreeDataContainer container = (SlidingMenuTreeDataContainer) data;
        mTitle.setText(container.getName());
        if (!data.isExpandable()) {
            mDropDownView.setVisibility(INVISIBLE);
        } else {
            mDropDownView.setVisibility(VISIBLE);
            if (mCategory.isExpanded()) {
                mDropDownView.setImageResource(R.drawable.arrow_down);
            } else {
                mDropDownView.setImageResource(R.drawable.arrow_right);
            }
        }
        final int paddingLeft = Utils.convertDipToPixels(getContext(), 15 * container.getIndent());
        setPadding(paddingLeft, 0, 0, 0);
    }

    public void setChecked(boolean isChecked) {
        mCheckbox.setChecked(isChecked);
    }

    @Override
    public ITreePureNode getData() {
        return mCategory;
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mCheckbox.setOnTouchListener(listener);
        mTitle.setOnTouchListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.img_drop_down:
        case R.id.icon:
            if (mListener != null) {
                mListener.onCategoryOpen(mCategory);
            }
            break;

        case R.id.txt_title:
            mCheckbox.setChecked(!mCheckbox.isChecked());
        default:
            break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton v, boolean isChecked) {
        if (mListener != null) {
            mListener.onCategoryChecked(mCategory, isChecked);
        }
    }

    public void setCustomListener(OnCategoryListItemListener listener) {
        mListener = listener;
    }
}