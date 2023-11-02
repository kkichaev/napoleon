package com.ashberrysoft.leadertask.modern.view.list_item;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.dialog.AddCategoryDialog;
import com.ashberrysoft.leadertask.modern.dialog.AddMarkerDialog;
import com.ashberrysoft.leadertask.modern.dialog.AddProjectDialog;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.utils.Utils;

public class HeaderMenuListItemView extends BaseMenuListItemView {

    // VIEW's
    private final TextView mName;
    private final ImageView mDropDown;
    private final RelativeLayout mAddLayout;
    private BaseMenuItem mMenuItem;
    private boolean isDropped = false;
    private final Fragment mTarget;

    public HeaderMenuListItemView(Context context, OnMenuListItemListener listener, Fragment target) {
        super(context, listener);

        inflate(getContext(), R.layout.list_item_header_menu, this);
        this.setOrientation(LinearLayout.VERTICAL);
        mName = (TextView) findViewById(R.id.text_view);
        mTarget = target;
        mAddLayout = (RelativeLayout) findViewById(R.id.add_layout);
        mDropDown = (ImageView) findViewById(R.id.drop_down);
        this.setOnClickListener(this);

        mAddLayout.setClickable(true);
        mAddLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (mMenuItem.getMenuItemType()) {
                    case HEADER_PROJECTS:
                        AddProjectDialog.newInstance(mTarget).showDialog(mTarget.getFragmentManager());
                        break;

                    case HEADER_CATEGORIES:
                        AddCategoryDialog.newInstance(mTarget).showDialog(mTarget.getFragmentManager());
                        break;

                    case HEADER_COLORS:
                        AddMarkerDialog.newInstance(mTarget).showDialog(mTarget.getFragmentManager());
                        break;

                    case HEADER_EMPS:
                        Utils.iWantToAddUsers(mTarget.getActivity(), mTarget);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void setData(BaseMenuItem menuItem, int i) {
        mMenuItem = menuItem;
        isDropped = LTSettings.getInstance().isDroppedHeader(menuItem);
        mName.setText(getResources().getString(menuItem.getMenuItemType().getNameId()));
        mDropDown.setImageResource(isDropped ? R.drawable.left_arrow : R.drawable.down_arrow );

        if (mMenuItem.getMenuItemType().equals(MenuItemType.HEADER_AVAILABLE_PROJECTS) || mMenuItem.getMenuItemType().equals(MenuItemType.HEADER_BY_ME) || mMenuItem.getMenuItemType().equals(MenuItemType.HEADER_FOR_ME)) {
            mAddLayout.setVisibility(GONE);
        }
    }

    @Override
    public void onClick(View v) {
        isDropped = !isDropped;
        mDropDown.setImageResource(isDropped ? R.drawable.left_arrow : R.drawable.down_arrow );
        getListener().onDropDownClickHeader(mMenuItem, isDropped);
    }

    @Override
    public boolean onLongClick(View v) {

        return false;
    }
}