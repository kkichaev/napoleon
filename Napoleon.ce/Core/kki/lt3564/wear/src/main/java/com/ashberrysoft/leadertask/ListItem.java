package com.ashberrysoft.leadertask;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.wearable.view.WearableListView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;

import java.io.File;

import static android.R.attr.id;
import static android.graphics.drawable.Drawable.createFromPath;
import static com.ashberrysoft.leadertask.ListActivity.canSwipeToRefresh;
import static java.security.AccessController.getContext;


public class ListItem extends LinearLayout  implements View.OnClickListener, WearableListView.OnCenterProximityListener {

    @Override
    public void onClick(View view) {
        mListener.onTaskClick(mData, mId);
    }

    public interface OnClickTaskItemListener {
        void onTaskClick(String data,int id);
    }

    @Override
    public void onCenterPosition(boolean b) {
        //Animation example to be ran when the view becomes the centered one
        //image.animate().scaleX(1f).scaleY(1f).alpha(1);
        mTextView.animate().scaleX(1f).scaleY(1f).alpha(1);
        mainFotoContainer.animate().scaleX(1f).scaleY(1f).alpha(1);
        twoFotoContainer.animate().scaleX(1f).scaleY(1f).alpha(1);
        //if (b) {
            //setBackgroundColor(false);
        //}
        mLinearLayout.setBackgroundColor(Color.rgb(241, 241, 241));
    }

    @Override
    public void onNonCenterPosition(boolean b) {
        //Animation example to be ran when the view is not the centered one anymore
        //image.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        mTextView.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        mainFotoContainer.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);
        twoFotoContainer.animate().scaleX(0.8f).scaleY(0.8f).alpha(0.6f);

        mLinearLayout.setBackgroundColor(Color.WHITE);
    }

    private String mData;
    private int mId;
    private boolean mIsTodayList;

    private TextView mTextView;
    private LinearLayout mLinearLayout;
    private SwipeLayout mSwipeLayout;
    private ListActivity mListActivity;

    private final ImageView mIvUser;
    private final ImageView mIvUserCustom;
    private final ImageView mIvUserTwo;
    private final ImageView mIvUserCustomTwo;
    private final RelativeLayout mainFotoContainer;
    private final RelativeLayout twoFotoContainer;

    private final OnClickTaskItemListener mListener;


    public ListItem(Context context, OnClickTaskItemListener listener) {
        super(context);
        {
            inflate(getContext(), R.layout.list_item_wear, this);
            this.setOrientation(LinearLayout.VERTICAL);
            mListener = listener;
        }

        try {
            mListActivity = (ListActivity) context;
        } catch (Exception e) {

        }
        mTextView = (TextView) findViewById(R.id.text_view);
        //mTextView.setPivotY(100);
        //mTextView.setPivotX(0);
        mLinearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        mIvUser = (ImageView) findViewById(R.id.img_user);
        mIvUserCustom = (ImageView) findViewById(R.id.img_user_custom);
        mIvUserTwo = (ImageView) findViewById(R.id.img_user_two);
        mIvUserCustomTwo = (ImageView) findViewById(R.id.img_user_custom_two);

        mainFotoContainer = (RelativeLayout) findViewById(R.id.img_user_container);
        twoFotoContainer = (RelativeLayout) findViewById(R.id.img_user_two_container);

        mLinearLayout.setOnClickListener(this);

        mSwipeLayout = (SwipeLayout) findViewById(R.id.task_swipe_layout);
        mSwipeLayout.setFocusable(true);
        mSwipeLayout.setFocusableInTouchMode(true);
        //set show mode.
        //mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
        //mSwipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_task_wrapper_right));
        mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
                if (mSwipeLayout.getDragEdge() != SwipeLayout.DragEdge.Left) {
                    //ListActivity.mSwipeRefreshLayout.setEnabled(TasksFragment.canSwipeToRefresh); // включаем SwipeRefresh если вы вверху списка
                    ListActivity.mSwipeRefreshLayout.setEnabled(canSwipeToRefresh); // включаем SwipeRefresh если вы вверху списка
                }
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                ListActivity.mSwipeRefreshLayout.setEnabled(false); // отключаем SwipeRefresh
                //TasksFragment.mTempTask = mTask;
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                layout.close();
                android.util.Log.v("Tedorius", "Завершили задачу");
                if (mListActivity != null) {
                    if (mIsTodayList) {
                        mListActivity.sendMessage(false, true, Utils.getTaskUidFromIdInToday(mId, getContext()), mId);
                    } else {
                        mListActivity.sendMessage(false, true, Utils.getTaskUidFromIdInForMe(mId, getContext()), mId);
                    }
                }
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });
    }

    public void setData(String taskName, int id, boolean isTodayList) {
        mData = taskName;
        mId = id;
        mTextView.setText(mData);
        mIsTodayList = isTodayList;
        setUsersFoto(id, isTodayList);
    }

    private void setUsersFoto(int id, boolean isTodayList) {
        String mUser = Utils.getUserName(getContext());
        String mCustomer = "";
        String mPerformer = "";
        if (isTodayList) {
            mCustomer = Utils.getTaskCustomerFromIdInToday(id, getContext());
            mPerformer = Utils.getTaskPerformerFromIdInToday(id, getContext());
        } else {
            mCustomer = Utils.getTaskCustomerFromIdInForMe(id, getContext());
            mPerformer = mUser;
        }

        mIvUserTwo.setVisibility(GONE);
        mIvUserCustomTwo.setVisibility(GONE);
        twoFotoContainer.setVisibility(GONE);

        if (mUser.equals(mCustomer)) {
            if (mUser.equals(mPerformer)) {
                if (mIvUser.getVisibility() != View.GONE) {
                    mIvUser.setVisibility(View.GONE);
                    mIvUserCustom.setVisibility(View.GONE);
                    mainFotoContainer.setVisibility(GONE);
                    twoFotoContainer.setVisibility(GONE);
                }

            } else {
                setPerformerFoto(mIvUser, mIvUserCustom, mPerformer, 0);

                if (mIvUser.getVisibility() != View.VISIBLE) {
                    mIvUser.setVisibility(View.VISIBLE);
                    mainFotoContainer.setVisibility(VISIBLE);
                }
            }
        } else {
            if (mUser.equals(mPerformer)) {
                setPerformerFoto(mIvUser, mIvUserCustom, mCustomer, 1);
            } else {
                if (!mCustomer.equals(mPerformer)) {
                    mIvUserCustomTwo.setVisibility(VISIBLE);
                    mIvUserTwo.setVisibility(VISIBLE);
                    twoFotoContainer.setVisibility(View.VISIBLE);
                    setPerformerFoto(mIvUserTwo, mIvUserCustomTwo, mPerformer, 2);
                }

                setPerformerFoto(mIvUser, mIvUserCustom, mCustomer, 2);
            }

            if (mIvUser.getVisibility() != View.VISIBLE) {
                mIvUser.setVisibility(View.VISIBLE);
                mainFotoContainer.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setPerformerFoto(ImageView image, ImageView imageCustom, String userEmail, int type) {
        //
        imageCustom.setVisibility(GONE);
        try {
            RoundedBitmapDrawable roundedBitmapDrawable = Utils.getFotoBitmapFromFolder(mListActivity, userEmail);
            if (roundedBitmapDrawable != null) {
                image.setImageDrawable(roundedBitmapDrawable);
                imageCustom.setVisibility(VISIBLE);
                switch (type) {
                    case 0:
                        imageCustom.setImageResource(R.mipmap.emp_circle_from_me);
                        break;

                    case 1:
                        imageCustom.setImageResource(R.mipmap.emp_circle_to_me);
                        break;

                    case 2:
                    default:
                        imageCustom.setImageResource(R.mipmap.emp_circle_simple);
                        break;
                }

            } else {
                switch (type) {
                    case 0:
                        image.setImageResource(R.mipmap.emp_from_me);
                        break;

                    case 1:
                        image.setImageResource(R.mipmap.emp_to_me);
                        break;

                    case 2:
                    default:
                        image.setImageResource(R.mipmap.emp_simple);
                        break;
                }
            }
        }
        catch (Exception e) {
            imageCustom.setVisibility(GONE);
            switch (type) {
                case 0:
                    image.setImageResource(R.mipmap.emp_from_me);
                    break;

                case 1:
                    image.setImageResource(R.mipmap.emp_to_me);
                    break;

                case 2:
                default:
                    image.setImageResource(R.mipmap.emp_simple);
                    break;
            }
        }
        //
    }
}
