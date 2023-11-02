package com.ashberrysoft.leadertask.modern.view.list_item;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.enums.MenuItemType;
import com.ashberrysoft.leadertask.modern.domains.menu.BaseMenuItem;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.modern.loader.EmailsMenuItem;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;

import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import static android.R.attr.padding;
import static com.ashberrysoft.leadertask.enums.MenuItemType.ADD_CATEGORY;
import static com.ashberrysoft.leadertask.enums.MenuItemType.ADD_EMP;
import static com.ashberrysoft.leadertask.enums.MenuItemType.FOCUS;
import static com.ashberrysoft.leadertask.enums.MenuItemType.OVERDUE;
import static com.ashberrysoft.leadertask.enums.MenuItemType.UNREAD;

public class MenuListItemView extends BaseMenuListItemView {

    // VIEW's
    private final View mLayout;
    private final View mLayoutAddNew;
    private final ImageView mImage;
    private final ImageView mCircleImageCustom;
    private final TextView mName;
    private final TextView mNameAdd;
    private final TextView mUnreadCount;
    private final TextView mAllCount;
    private final LinearLayout mRightContainer;
    private final RelativeLayout mDropDownLayout;
    private final ImageView mDropDown;
    private LTApplication mApp;

    // VALUE's
    private final LTSettings mSettings;
    private final int mUnivPadding;
    private BaseMenuItem mMenuItem;
    private int id;

    public MenuListItemView(Context context, OnMenuListItemListener listener) {
        super(context, listener);
        {
            inflate(getContext(), R.layout.list_item_menu, this);
            this.setOrientation(LinearLayout.VERTICAL);
            mApp = (LTApplication) getContext().getApplicationContext();
        }

        mLayout = findViewById(R.id.linear_layout);
        mLayoutAddNew = findViewById(R.id.linear_layout_add);
        mImage = (ImageView) findViewById(R.id.image_view);
        mCircleImageCustom = (ImageView) findViewById(R.id.iv_img_custom);
        mName = (TextView) findViewById(R.id.text_view);
        mNameAdd = (TextView) findViewById(R.id.text_view_add);
        mUnreadCount = (TextView) findViewById(R.id.unread_count);
        mAllCount = (TextView) findViewById(R.id.all_count);
        mDropDown = (ImageView) findViewById(R.id.drop_down);
        mRightContainer = (LinearLayout) findViewById(R.id.right_menu_item_layout);
        mDropDownLayout = (RelativeLayout) findViewById(R.id.drop_down_layout);

        this.setOnClickListener(this);
        this.setOnLongClickListener(this);
        mRightContainer.setOnClickListener(this);

        mSettings = LTSettings.getInstance(getContext());
        mUnivPadding = getResources().getDimensionPixelSize(R.dimen.univ_padding);
    }

    @Override
    public void setData(BaseMenuItem menuItem, int i) {
    	mMenuItem = menuItem;
        mLayout.setVisibility(VISIBLE);
        mLayoutAddNew.setVisibility(GONE);
        id = i;
        if (mSettings.getMenuItem().getMenuItemType() == MenuItemType.INBOX ||
                mSettings.getMenuItem().getMenuItemType() == MenuItemType.TODAY ||
                mSettings.getMenuItem().getMenuItemType() == UNREAD ||
                mSettings.getMenuItem().getMenuItemType() == MenuItemType.INWORK ||
                mSettings.getMenuItem().getMenuItemType() == MenuItemType.READY ||
                mSettings.getMenuItem().getMenuItemType() == OVERDUE ||
                mSettings.getMenuItem().getMenuItemType() == FOCUS) {
            if (mSettings.getMenuItem().getMenuItemType() == mMenuItem.getMenuItemType()) {
                this.setBackgroundColor(getResources().getColor(R.color.checked_menu_color));
                MenuFragment.lastCheckedMenuItemUUID = mMenuItem.getUid();
            } else {
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
            }
        } else {
            if (mSettings.getMenuItem().getUniqueId() == mMenuItem.getUniqueId() &&
                    (mMenuItem.getName() != null ? mMenuItem.getName().equals(mSettings.getMenuItem().getName()) : false )) {
                MenuFragment.lastCheckedMenuItemUUID = mMenuItem.getUid();
                if (mSettings.getMenuItem().getMenuItemType() == menuItem.getMenuItemType()) {
                    this.setBackgroundColor(getResources().getColor(R.color.checked_menu_color));
                } else {
                    this.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
                }
            } else {
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_item));
            }
        }

        mCircleImageCustom.setVisibility(GONE);
        mImage.setVisibility(GONE);
        if(mMenuItem.getMenuItemType().equals(MenuItemType.CATEGORIES)){
            mImage.setVisibility(VISIBLE);
            try {
                Category category = DbHelper.getInstance(getContext()).getCategoryByUUId(UUID.fromString(menuItem.getUid())) ;
                if(category.getColor() != null && !category.getColor().equals(Category.NO_COLOR)) {
                    mImage.setImageBitmap(Utils.getCategoryDrawable(mApp, category.getColor()));
                }
                else {
                    //mImage.setImageResource(MenuItemType.CATEGORIES.getImageId());
                    mImage.setImageBitmap(Utils.getCategoryDrawable(mApp, null));

                }
            }
            catch (Exception e) {
                //mImage.setImageResource(MenuItemType.CATEGORIES.getImageId());
                mImage.setImageBitmap(Utils.getCategoryDrawable(mApp, null));
                mCircleImageCustom.setVisibility(GONE);
            }
        } else if(mMenuItem.getMenuItemType().equals(MenuItemType.COLOR)){
            mImage.setVisibility(VISIBLE);
            try {
                Marker color = DbHelper.getInstance(getContext()).getMarkerByUUId(UUID.fromString(menuItem.getUid())) ;
                if(color.getBackColor() != null && !color.getBackColor().equals(Marker.NO_COLOR)) {
                    mImage.setImageBitmap(Utils.getColorDrawable(mApp, color.getBackColor()));
                }
                else {
                    mImage.setImageBitmap(Utils.getColorDrawable(mApp, null));

                }
            }
            catch (Exception e) {
                mImage.setImageBitmap(Utils.getColorDrawable(mApp, null));
                mCircleImageCustom.setVisibility(GONE);
            }
        } else if(mMenuItem.getMenuItemType().equals(MenuItemType.BY_ME) || mMenuItem.getMenuItemType().equals(MenuItemType.FOR_ME) || mMenuItem.getMenuItemType().equals(MenuItemType.EMP)) {
            mImage.setVisibility(VISIBLE);
            mCircleImageCustom.setVisibility(GONE);
            try {
                Emp emp = DbHelper.getInstance(getContext()).getEmpByLogin(menuItem.getUid());
                RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, emp.getLogin());
                if(roundedBitmapDrawable != null) {
                    mImage.setImageDrawable(roundedBitmapDrawable);
                    mCircleImageCustom.setVisibility(VISIBLE);
                    int padding7 = getResources().getDimensionPixelSize(R.dimen.univ_padding_seven);
                    mImage.setPadding(padding7,padding7,padding7,padding7);
                    if (mMenuItem.getMenuItemType().equals(MenuItemType.BY_ME)) {
                        mCircleImageCustom.setImageResource(R.drawable.emp_circle_from_me);
                    }
                    else {
                        if (mMenuItem.getMenuItemType().equals(MenuItemType.EMP)) {
                            mCircleImageCustom.setImageResource(R.drawable.emp_circle_simple);
                        } else {
                            mCircleImageCustom.setImageResource(R.drawable.emp_circle_to_me);
                        }
                    }
                } else {
                    int padding = getResources().getDimensionPixelSize(R.dimen.univ_padding_five);
                    mImage.setPadding(padding,padding,padding,padding);
                    if (mMenuItem.getMenuItemType().equals(MenuItemType.BY_ME)) {
                        mImage.setImageResource(R.drawable.emp_from_me);
                    }
                    else {
                        if (mMenuItem.getMenuItemType().equals(MenuItemType.EMP)) {
                            mImage.setImageResource(R.drawable.emp_simplenew);
                        } else {
                            mImage.setImageResource(R.drawable.emp_to_me);
                        }
                    }
                }
            }
            catch (Exception e) {
                mImage.setImageResource(mMenuItem.getMenuItemType().getImageId());
                mCircleImageCustom.setVisibility(GONE);
            }

        } else if(mMenuItem.getMenuItemType().equals(MenuItemType.ADD_PROJECT) || mMenuItem.getMenuItemType().equals(MenuItemType.ADD_CATEGORY)
                || mMenuItem.getMenuItemType().equals(MenuItemType.ADD_COLOR) || mMenuItem.getMenuItemType().equals(MenuItemType.ADD_EMP)) {

            mLayout.setVisibility(GONE);
            mLayoutAddNew.setVisibility(VISIBLE);



            /*mImage.setVisibility(VISIBLE);
            mCircleImageCustom.setVisibility(GONE);
            mImage.setImageResource(mMenuItem.getMenuItemType().getImageId());*/

        } else if(mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS) || mMenuItem.getMenuItemType().equals(MenuItemType.PROJECTS_SHARED)) {
                mImage.setVisibility(VISIBLE);
                mImage.setImageResource(mMenuItem.getMenuItemType().getImageId());
        } else if(mMenuItem.getMenuItemType().equals(MenuItemType.AVAILABLE_PROJECTS)) {
                mImage.setVisibility(VISIBLE);
                mImage.setImageResource(mMenuItem.getMenuItemType().getImageId());
        } else {
            mImage.setVisibility(VISIBLE);
            mCircleImageCustom.setVisibility(GONE);
            mImage.setImageResource(mMenuItem.getMenuItemType().getImageId());
        }

        if (mMenuItem.getName() != null) {
            mName.setText(mMenuItem.getName());

        } else if (mMenuItem.getMenuItemType().getNameId() != 0) {
            mName.setText(mMenuItem.getMenuItemType().getNameId());

        } else {
            mName.setText(null);
        }

        if (mMenuItem.getMenuItemType().equals(MenuItemType.TODAY)) {
            mName.setText(TimeHelper.getInstance().getCuteDateTitle(new Date(System.currentTimeMillis())));
        }

        if(mMenuItem.getMenuItemType().equals(MenuItemType.ADD_PROJECT) || mMenuItem.getMenuItemType().equals(MenuItemType.ADD_CATEGORY)
                || mMenuItem.getMenuItemType().equals(MenuItemType.ADD_COLOR) || mMenuItem.getMenuItemType().equals(MenuItemType.ADD_EMP)) {
            if (mMenuItem.getName() != null) {
                mNameAdd.setText(mMenuItem.getName());

            } else if (mMenuItem.getMenuItemType().getNameId() != 0) {
                mNameAdd.setText(mMenuItem.getMenuItemType().getNameId());

            } else {
                mNameAdd.setText(null);
            }
            this.setBackgroundColor(getResources().getColor(R.color.white));
        }

        if (mMenuItem.hasBelow()) {
            mDropDown.setImageResource(mMenuItem.isOpened() ? R.drawable.down_arrow
                    : R.drawable.left_arrow);
            mRightContainer.setClickable(true);
            mDropDownLayout.setVisibility(VISIBLE);

        } else {
            mRightContainer.setClickable(false);
            mDropDownLayout.setVisibility(GONE);
        }

        final int levelLeft = mUnivPadding * mMenuItem.getLevel();
        mLayout.setPadding(levelLeft, 0, 0, 0);

        //

        int countChilds;
        final int countUnreaded;
        countChilds = mMenuItem.getTasksUncompleted();
        if (mMenuItem.getMenuItemType().equals(MenuItemType.UNREAD)) {
            countChilds = mMenuItem.getTasksUncompletedUnreaded();
        }

        if (mMenuItem.getMenuItemType().equals(MenuItemType.FOCUS)) {
            countChilds = mMenuItem.getTasksFocus();
        }

        if (mMenuItem.getMenuItemType().equals(MenuItemType.EMAILS)) {
            countChilds = ((EmailsMenuItem)mMenuItem).countItems;
        }
        //

        /*int countChilds;
        final int countUnreaded;
        countChilds = mSettings.isMakeTaskHide() ? mMenuItem.getTasksUncompleted() : mMenuItem.getTasks();

        if(mMenuItem.getMenuItemType().equals(MenuItemType.INBOX) || mMenuItem.getMenuItemType().equals(MenuItemType.INWORK) || mMenuItem.getMenuItemType().equals(MenuItemType.READY)  || mMenuItem.getMenuItemType().equals(MenuItemType.COLOR) || mMenuItem.getMenuItemType().equals(MenuItemType.EMP)) {
            countChilds = mMenuItem.getTasksUncompleted();
        } else {
            if (mMenuItem.getMenuItemType().equals(UNREAD)) {
                countChilds = mMenuItem.getTasksUncompletedUnreaded();
            }
        }
*/
        //

        if (mMenuItem.getTasksUncompletedUnreaded() < 0) {
            mMenuItem.getMenuItemType().setTasksUncompletedUnreaded(0);
        }
        countUnreaded = mMenuItem.getTasksUncompletedUnreaded();

        if (countChilds == 0 /*|| mMenuItem.getMenuItemType().equals(MenuItemType.UNREAD)*/) {
            mAllCount.setVisibility(GONE);
        } else {
            mAllCount.setText(countChilds > 999 ? SharedStrings.NUMBER_999 : String.valueOf(countChilds));

            if (mSettings.showTaskCountInNavigator()) { // настройки
                mAllCount.setVisibility(VISIBLE);
            } else {
                mAllCount.setVisibility(GONE);
            }
        }

        if(countUnreaded == 0) {
            mName.setTypeface(Typeface.DEFAULT);
        }
        else {
            mName.setTypeface(Typeface.DEFAULT_BOLD);
            //mUnreadCount.setText(countUnreaded > 99 ? SharedStrings.NUMBER_99 : String.valueOf(countUnreaded));
        }

        /*if (mSettings.showTaskCountInNavigator()) { // настройки
            mUnreadCount.setVisibility(VISIBLE);
        } else {*/
            mUnreadCount.setVisibility(GONE);
        //}
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.right_menu_item_layout) {
            getListener().onDropDownClick(mMenuItem, !mMenuItem.isOpened());
        } else {
            getListener().onMenuClick(mMenuItem, id);
            this.setBackgroundColor(getResources().getColor(R.color.checked_menu_color));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v.getId() == R.id.right_menu_item_layout) {
            getListener().onDropDownClick(mMenuItem, !mMenuItem.isOpened());
        } else {
            if (mMenuItem.getMenuItemType().equals(MenuItemType.AVAILABLE_PROJECTS) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.CALENDAR_DAY) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.TODAY) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.INBOX) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.INWORK) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.READY)  ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.UNREAD) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.OVERDUE) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.ADD_PROJECT)||
                    mMenuItem.getMenuItemType().equals(MenuItemType.ADD_CATEGORY) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.ADD_COLOR) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.ADD_EMP) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.FOCUS) ||
                    mMenuItem.getMenuItemType().equals(MenuItemType.EMAILS)) {
                return false;
            } else {
                getListener().onMenuLongClick(v, mMenuItem, id);
            }
        }
        return true;
    }
}