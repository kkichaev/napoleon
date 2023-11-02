package com.ashberrysoft.leadertask.views;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.activities.FeaturesActivity.FeatureType;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.content_providers.LionMetaData;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Contact;
import com.ashberrysoft.leadertask.domains.ordinary.ContactsGroup;
import com.ashberrysoft.leadertask.domains.ordinary.Emp;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Project;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache.LTaskCacheHolder;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.v2soft.AndLib.ui.views.IDataView;

public class ListItemSearchView<DATA> extends LinearLayout//
        implements View.OnClickListener, View.OnLongClickListener, IDataView<DATA> {

    public interface OnSearchViewListener<DATA> {
        public void onSearchContactViewClick(Contact data, int position);

        public void onSearchContactViewLongClick(View v, DATA data, int position);

        public void onSearchTaskViewClick(LTask data, int position,  boolean hasChilds);

        public void onSearchTaskViewLongClick(View v, LTask data, int position,  boolean hasChilds);
    }

    private static final int BADGE_GREEN_COLOR = Color.argb(200, 150, 150, 150);
    private static final int BADGE_BROWN_COLOR = Color.argb(200, 239, 108, 0);
    // VIEW's
    private TextView mName;
    private TextView mSubName;
    private ImageView mImage;
    private ImageView mIvUser;
    private ImageView mIvUserCustom;
    private ImageView mIvUserTwo;
    private ImageView mIvUserCustomTwo;
    private RelativeLayout mainFotoContainer;
    private RelativeLayout twoFotoContainer;
    private EmployeeCache mEmployeeCache;
    private BadgeView mBvChildsCount;
    private View mIvAttachedFiles;
    private View mIvTaskComment;
    private View mIvMessages;

    // VALUE's
    private LTSettings mSettings;
    private DATA mData;
    private int mPosition;
    private int mItemType;
    private LTApplication mApp;
    private boolean mHasChilds;
    private LTaskCacheHolder mCacheHolder;
    private LTaskCache mTaskCache;

    // LISTENER
    private OnSearchViewListener<DATA> mListener;

    public ListItemSearchView(Context context) {
        super(context);
        initialization();
    }

    public ListItemSearchView(Context context, OnSearchViewListener<DATA> listener) {
        super(context);
        initialization();
        setCustomListener(listener);
        mApp = (LTApplication) getContext().getApplicationContext();
        mEmployeeCache = EmployeeCache.getInstance(getContext());
        mTaskCache = LTaskCache.getInstance(getContext());
    }

    private void initialization() {
        inflate(getContext(), R.layout.list_item_view_search, this);
        this.setOrientation(VERTICAL);
        this.setOnClickListener(this);
        this.setOnLongClickListener(this);

        mSettings = LTSettings.getInstance(getContext());

        mName = (TextView) findViewById(R.id.tv_feature_name);
        mSubName = (TextView) findViewById(R.id.tv_feature_sub_name);
        mImage = ((ImageView) findViewById(R.id.iv_task_status));
        mIvUser = (ImageView) findViewById(R.id.img_user);
        mBvChildsCount = new BadgeView(getContext(), mImage);
        mIvUserCustom = (ImageView) findViewById(R.id.img_user_custom);
        mIvUserTwo = (ImageView) findViewById(R.id.img_user_two);
        mIvUserCustomTwo = (ImageView) findViewById(R.id.img_user_custom_two);
        mainFotoContainer = (RelativeLayout) findViewById(R.id.img_user_container);
        twoFotoContainer = (RelativeLayout) findViewById(R.id.img_user_two_container);

        mIvAttachedFiles = findViewById(R.id.attached_files);
        mIvTaskComment = (ImageView) findViewById(R.id.img_comments_text);
        mIvMessages = (ImageView) findViewById(R.id.img_messages);

        mName.setTextColor(mSettings.isThemeDark() ? Color.WHITE : Color.BLACK);
        mSubName.setTextColor(mSettings.isThemeDark() ? Color.WHITE : Color.BLACK);
        //
        mBvChildsCount.setTextColor(Color.WHITE);
        mBvChildsCount.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
        final int paddingH = getResources().getDimensionPixelSize(R.dimen.univ_padding_small);
        final int paddingB = getResources().getDimensionPixelSize(R.dimen.univ_padding_tiny);
        final int tsBadge = getResources().getDimensionPixelSize(R.dimen.text_size_less);
        mBvChildsCount.setPadding(paddingH, 0, paddingH, paddingB);
        mBvChildsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, tsBadge);
    }

    public void setData(DATA data, int type) {
        mData = data;
        mItemType = type;

        if (mItemType == 0) {
            setTaskData((LTask) mData);
            setChildsCount();
        } else {
            setContactData((Contact) mData);
        }


    }

    private TaskTotalLink findChilds() {
        Cursor c = null;
        TaskTotalLink totalLink = null;
        try {
            LTask task = (LTask) mData;
            c = getContext().getContentResolver().query(LionMetaData.TaskTotalLinkContract.CONTENT_URI, null, "uid='"+task.getIdTask()+"'", null, null);
            if (c != null) {
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    totalLink = new TaskTotalLink(c);
                }
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return totalLink;
    }

    private void setChildsCount() {
        try {
            final TaskTotalLink totalLink = findChilds();
            if (mHasChilds = totalLink != null) {
                final int count;
                final boolean unreaded;

                if (mSettings.isMakeTaskHide()) {
                    count = totalLink.getTasksUncompleted();
                    unreaded = totalLink.getTasksUncompletedUnreaded() > 0;

                } else {
                    count = totalLink.getTasks();
                    unreaded = totalLink.getTasksUnreaded() > 0;
                }

                mBvChildsCount.setBadgeBackgroundColor(unreaded ? BADGE_BROWN_COLOR : BADGE_GREEN_COLOR);

                if (mHasChilds = count > 0) {
                    mBvChildsCount.setText(count < 999 ? String.valueOf(count) : SharedStrings.NUMBER_999);
                    mBvChildsCount.show();

                } else if (mBvChildsCount.getVisibility() != View.GONE) {
                    mBvChildsCount.hide();
                }

            } else if (mBvChildsCount.getVisibility() != View.GONE) {
                mBvChildsCount.hide();
            }
        } catch (Exception e) {
            if (mBvChildsCount.getVisibility() != View.GONE) {
                mBvChildsCount.hide();
            }
        }
    }

    private void setTaskData(LTask task) {
        final TaskStatus status = TaskStatus.getTaskStatus(task);

        if(mTaskCache == null) {
            mTaskCache = LTaskCache.getInstance(getContext()); //добавлено ибо скорее всего потому что кэш не вызывается
        }
        mCacheHolder = mTaskCache.find(task.getIdTask());
        mIvUserTwo.setVisibility(GONE);
        mIvUserCustomTwo.setVisibility(GONE);
        twoFotoContainer.setVisibility(GONE);

        if (task.getSeriesType() == TaskSeriesCalculator.SeriesType.NONE.ordinal()) {
            mImage.setImageResource(status.getResId());

        } else {
            mImage.setImageResource(status.getSeriesResId());
        }
        mName.setText(task.getName());

        setFilesCount();
        setCommentsAndMessages(task);

        if (mSettings.getUserName().equals(task.getEmailCustomer())) {
            if (mSettings.getUserName().equals(task.getEmailPerformer())) {
                if (mIvUser.getVisibility() != View.GONE) {
                    mIvUser.setVisibility(View.GONE);
                    mIvUserCustom.setVisibility(View.GONE);
                    mainFotoContainer.setVisibility(GONE);
                    twoFotoContainer.setVisibility(GONE);
                    //mTvUser.setVisibility(View.GONE);
                }

            } else {
                //mTvUser.setText(mEmployeeCache.find(mTask.getEmailPerformer()));
                if(task.getPerformerReaded()) {
                    //mIvUser.setImageResource(R.drawable.tome_task);
                    setPerformerFoto(mIvUser, mIvUserCustom, task.getEmailPerformer(), true, 0);
                }
                else {
                    //mIvUser.setImageResource(R.drawable.performer_not_read_list);
                    setPerformerFoto(mIvUser, mIvUserCustom, task.getEmailPerformer(), false, 0);
                }

                if (mIvUser.getVisibility() != View.VISIBLE) {
                    mIvUser.setVisibility(View.VISIBLE);
                    mainFotoContainer.setVisibility(VISIBLE);
                    //mTvUser.setVisibility(View.VISIBLE);
                }
            }

        } else {
            final CharSequence customer = mEmployeeCache.find(task.getEmailCustomer());
            if (mSettings.getUserName().equals(task.getEmailPerformer())) {
                //mTvUser.setText(customer);
                setPerformerFoto(mIvUser, mIvUserCustom, task.getEmailCustomer(), false, 1);
                //mIvUser.setImageResource(R.drawable.fromme_task);

            } else {

                if (!task.getEmailCustomer().equals(task.getEmailPerformer())) {
                    mIvUserCustomTwo.setVisibility(VISIBLE);
                    mIvUserTwo.setVisibility(VISIBLE);
                    twoFotoContainer.setVisibility(View.VISIBLE);
                    setPerformerFoto(mIvUserTwo, mIvUserCustomTwo, task.getEmailPerformer(), false, 2);
                }

                //mTvUser.setText(mStringBuilder);
                //mIvUser.setImageResource(R.drawable.lock_task);
                setPerformerFoto(mIvUser, mIvUserCustom, task.getEmailCustomer(), false, 2);
            }

            if (mIvUser.getVisibility() != View.VISIBLE) {
                mIvUser.setVisibility(View.VISIBLE);
                mainFotoContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setFilesCount() {
        if (mCacheHolder != null) {
            Utils.changeVisibility(mIvAttachedFiles, mCacheHolder.isHasFiles() ? View.VISIBLE : View.GONE);
        }
    }

    private void setCommentsAndMessages(LTask task) {
        Utils.changeVisibility(mIvTaskComment, TextUtils.isEmpty(task.getComment()) ? View.GONE : View.VISIBLE);
        if (mCacheHolder != null) {
            Utils.changeVisibility(mIvMessages, mCacheHolder.isHasMessages() ? View.VISIBLE : View.GONE);
        }
    }

    private void setContactData(Contact contact) {
        LTApplication mApp = (LTApplication) getContext().getApplicationContext();
        mName.setText(contact.getTitle());
        mIvUser.setVisibility(GONE);
        mIvUserTwo.setVisibility(GONE);
        mIvUserCustomTwo.setVisibility(GONE);
        mIvUserCustom.setVisibility(GONE);
        twoFotoContainer.setVisibility(GONE);
        mainFotoContainer.setVisibility(GONE);

        RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, contact.getId().toString());
        if(roundedBitmapDrawable != null) {
            mImage.setImageDrawable(roundedBitmapDrawable);
        }
        else {

            if (contact.getEmailCreator().equals(LTSettings.getInstance().getUserName())) {
                switch (contact.getGender()) {
                    case 1:
                        mImage.setImageResource(R.drawable.c_men);
                        break;
                    case 2:
                        mImage.setImageResource(R.drawable.c_women);
                        break;
                    case 3:
                        mImage.setImageResource(R.drawable.c_org);
                        break;
                    default:
                        mImage.setImageResource(R.drawable.c_nobody);
                        return;
                }
            }
            else {
                switch (contact.getGender()) {
                    case 1:
                        mImage.setImageResource(R.drawable.c_men_avaleble);
                        break;
                    case 2:
                        mImage.setImageResource(R.drawable.c_women_avaleble);
                        break;
                    case 3:
                        mImage.setImageResource(R.drawable.c_org_avaleble);
                        break;
                    default:
                        mImage.setImageResource(R.drawable.c_nobody_avaleble);
                        return;
                }
            }
        }
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            if (mItemType != 0) {
                mListener.onSearchContactViewClick((Contact)mData, mPosition);
            } else {
                mListener.onSearchTaskViewClick((LTask) mData, mPosition, mHasChilds);
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mListener != null) {
            if (mItemType != 0) {
                mListener.onSearchContactViewLongClick(v, mData, mPosition);
            } else {
                mListener.onSearchTaskViewLongClick(v, (LTask) mData, mPosition, mHasChilds);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setData(DATA data) {

    }

    @Override
    public DATA getData() {
        return mData;
    }

    public void setCustomListener(OnSearchViewListener<DATA> listener) {
        mListener = listener;
    }

    private void setPerformerFoto(ImageView image, ImageView imageCustom, String userEmail, boolean isReaded, int type) {
        //
        imageCustom.setVisibility(GONE);
        try {
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mApp, userEmail);
            if (roundedBitmapDrawable != null) {
                image.setImageDrawable(roundedBitmapDrawable);
                imageCustom.setVisibility(VISIBLE);
                switch (type) {
                    case 0:
                        imageCustom.setImageResource(!isReaded ? R.drawable.emp_circle_from_me_not_readed : R.drawable.emp_circle_from_me);
                        break;

                    case 1:
                        imageCustom.setImageResource(R.drawable.emp_circle_to_me);
                        break;

                    case 2:
                    default:
                        imageCustom.setImageResource(R.drawable.emp_circle_simple);
                        break;
                }

            } else {
                switch (type) {
                    case 0:
                        image.setImageResource(isReaded ? R.drawable.emp_from_me : R.drawable.emp_from_me_not_readed);
                        break;

                    case 1:
                        image.setImageResource(R.drawable.emp_to_me);
                        break;

                    case 2:
                    default:
                        image.setImageResource(R.drawable.emp_simple);
                        break;
                }
            }
        }
        catch (Exception e) {
            imageCustom.setVisibility(GONE);
            switch (type) {
                case 0:
                    image.setImageResource(isReaded ? R.drawable.emp_from_me : R.drawable.emp_from_me_not_readed);
                    break;

                case 1:
                    image.setImageResource(R.drawable.emp_to_me);
                    break;

                case 2:
                default:
                    image.setImageResource(R.drawable.emp_simple);
                    break;
            }
        }
        //
    }
}