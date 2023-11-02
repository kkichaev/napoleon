package com.ashberrysoft.leadertask.modern.view.list_item;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.Animation.AnimationListener;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.application.LTApplication;
import com.ashberrysoft.leadertask.application.LTSettings;
import com.ashberrysoft.leadertask.data_providers.DbHelper;
import com.ashberrysoft.leadertask.data_providers.TaskSeriesCalculator.SeriesType;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Marker;
import com.ashberrysoft.leadertask.domains.ordinary.Status;
import com.ashberrysoft.leadertask.enums.TaskStatus;
import com.ashberrysoft.leadertask.modern.activity.BaseActivity;
import com.ashberrysoft.leadertask.modern.activity.SlidingActivity;
import com.ashberrysoft.leadertask.modern.cache.EmployeeCache;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache;
import com.ashberrysoft.leadertask.modern.cache.LTaskCache.LTaskCacheHolder;
import com.ashberrysoft.leadertask.modern.domains.auxiliary.CompletedTask;
import com.ashberrysoft.leadertask.modern.domains.link.TaskTotalLink;
import com.ashberrysoft.leadertask.modern.domains.lion.LTask;
import com.ashberrysoft.leadertask.modern.fragment.MenuFragment;
import com.ashberrysoft.leadertask.modern.fragment.TaskDrawerFragment;
import com.ashberrysoft.leadertask.modern.fragment.TasksFragment;
import com.ashberrysoft.leadertask.modern.helper.TaskHelper;
import com.ashberrysoft.leadertask.modern.helper.TimeHelper;
import com.ashberrysoft.leadertask.utils.SharedStrings;
import com.ashberrysoft.leadertask.utils.Utils;
import com.ashberrysoft.leadertask.views.BadgeView;
import com.ashberrysoft.leadertask.views.FlowLayout;
import com.daimajia.swipe.SwipeLayout;
import com.v2soft.AndLib.dao.ITreePureNode;

public class LTaskItemView extends LinearLayout//
        implements OnClickListener, OnLongClickListener {

    public interface OnLTaskItemListener {

        void onClickTask(LTask task, boolean hasChilds, int position);

        void onLongClickTask(LTask task, View v, boolean swipe);

        void onClickTaskStatus(LTask task, ImageView iv, boolean select);
    }

    private static final int BADGE_GREEN_COLOR = Color.argb(200, 150, 150, 150);
    private static final int BADGE_BROWN_COLOR = Color.argb(200, 239, 108, 0);
    static final int ANIMATION_DURATION = 300;
    private static final String LABEL_DIVIDER = ": ";
    private static final int[] STATE_PRESSED = new int[] { android.R.attr.state_pressed };
    private static final int[] STATE_UNPRESSED = new int[] { -android.R.attr.state_pressed };
    private static final Drawable TRANSPARENT = new ColorDrawable(Color.TRANSPARENT);

    // VIEW's
    private final LinearLayout mMainTaskContainer;
    // STATUS, CHILDS COUNT, NAME
    private final ImageView mIvStatus;
    private final BadgeView mBvChildsCount;
    private final TextView mTvTaskName;
    // STATUS LINE
    private final ImageView mIvUser;
    private final ImageView mIvUserCustom;
    private final ImageView mIvUserTwo;
    private final ImageView mIvUserCustomTwo;
    private final ImageView mIvFocus;
    //private final TextView mTvUser;
    private final View mIvTerm;
    private final TextView mTvTerm;
    private final View mIvTermCustomer;
    private final TextView mTvTermCustomer;
    private final View mIvAttachedFiles;
    private TextView mTimeTextView;
    private final View mIvTaskComment;
    private final View mIvMessages;
    private final RelativeLayout mainFotoContainer;
    private final RelativeLayout twoFotoContainer;
    // PLENTY
    private final FlowLayout mCategories;
    private final TextView[] mModifiedViews;

    // VALUE's
    private final LTask mTask;

    private final LTSettings mSettings;
    private LTaskCache mTaskCache;
    private final EmployeeCache mEmployeeCache;
    private final TimeHelper mTimeHelper;

    private final StringBuilder mStringBuilder;
    private final int mColorCompleted;
    private final Calendar mCalendar;

    private LTaskCacheHolder mCacheHolder;
    private boolean mHasChilds;
    private int mPosition;
    private SwipeLayout mSwipeLayout;
    private FrameLayout mLeftSlider;
    private FrameLayout mRightSlider;
    private boolean canSwipeToRefresh = true;

    //private final WeakReference<FragmentManager> mManager;
    private float mX;
    private float mY;
    private boolean firstTouch = true;
    private LTApplication mApp;

    // LISTENER
    private OnLTaskItemListener mListener;



    //runs without a timer by reposting this handler at the end of the runnable
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
            int min = mTask.getPlan();
            if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                // добавить тайм тикер чтобы обновлять
                int wasInWork = mTask.getTime() + (int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone() - mTask.getInWorkTime()) / 1000); // сек

                if (getContext() == null) {

                    timerHandler.removeCallbacks(timerRunnable);
                    return;
                }

                mTimeTextView.setText(Utils.getTextInWork(getContext(), wasInWork, min, mTask.getStatus()));
            }

            timerHandler.postDelayed(this, 1000);
        }
    };

    public LTaskItemView(Context context, OnLTaskItemListener listener) {
        this(context);
        mListener = listener;
        mApp = (LTApplication) getContext().getApplicationContext();
    }

    public LTaskItemView(Context context) {
        super(context, null, R.style.mainSelectableItemBackground);
        {
            inflate(getContext(), R.layout.view_task_new, this);
            this.setOrientation(HORIZONTAL);
        }
        mMainTaskContainer = (LinearLayout) findViewById(R.id.main_task_info_container);
        mIvStatus = (ImageView) findViewById(R.id.iv_task_status);
        // StartSmartAnimation.startAnimation(this, AnimationType.FadeOut,300,0,true);
        mBvChildsCount = new BadgeView(getContext(), mIvStatus);
        mTvTaskName = (TextView) findViewById(R.id.text_name);

        mIvUser = (ImageView) findViewById(R.id.img_user);
        mIvFocus = (ImageView) findViewById(R.id.img_focus);
        mIvUserCustom = (ImageView) findViewById(R.id.img_user_custom);
        mIvUserTwo = (ImageView) findViewById(R.id.img_user_two);
        mIvUserCustomTwo = (ImageView) findViewById(R.id.img_user_custom_two);
        //mTvUser = (TextView) findViewById(R.id.text_user);
        mIvTerm = findViewById(R.id.img_term);
        mTvTerm = (TextView) findViewById(R.id.text_term);
        mIvTermCustomer = findViewById(R.id.img_term_customer);
        mTvTermCustomer = (TextView) findViewById(R.id.text_term_customer);
        mIvAttachedFiles = findViewById(R.id.attached_files);
        mIvTaskComment = (ImageView) findViewById(R.id.img_comments_text);
        mIvMessages = (ImageView) findViewById(R.id.img_messages);
        mainFotoContainer = (RelativeLayout) findViewById(R.id.img_user_container);
        twoFotoContainer = (RelativeLayout) findViewById(R.id.img_user_two_container);

        mCategories = (FlowLayout) findViewById(R.id.categories_flow_layout);
        mModifiedViews = new TextView[] { mTvTerm, mTvTermCustomer };

        {
            mBvChildsCount.setTextColor(Color.WHITE);
            mBvChildsCount.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
            final int paddingH = getResources().getDimensionPixelSize(R.dimen.univ_padding_small);
            final int paddingB = getResources().getDimensionPixelSize(R.dimen.univ_padding_tiny);
            final int tsBadge = getResources().getDimensionPixelSize(R.dimen.text_size_less);
            mBvChildsCount.setPadding(paddingH, 0, paddingH, paddingB);
            mBvChildsCount.setTextSize(TypedValue.COMPLEX_UNIT_PX, tsBadge);
        }

        mIvStatus.setOnClickListener(this);
        mMainTaskContainer.setOnClickListener(this);
        mMainTaskContainer.setOnLongClickListener(this);

        mTask = new LTask();

        mSettings = LTSettings.getInstance(getContext());
        mTaskCache = LTaskCache.getInstance(getContext());
        mEmployeeCache = EmployeeCache.getInstance(getContext());
        mTimeHelper = TimeHelper.getInstance();

        mStringBuilder = new StringBuilder();
        mColorCompleted = getResources().getColor(R.color.gray_task_complete);
        mCalendar = Calendar.getInstance(TimeHelper.DEFAULT_TIME_ZONE);
        mCalendar.setTimeInMillis(TimeHelper.currentTimeMillisWithoutTimeZone());

        mLeftSlider =  (FrameLayout) findViewById(R.id.bottom_task_wrapper_right2);
        mRightSlider =  (FrameLayout) findViewById(R.id.bottom_task_wrapper_right);
        mSwipeLayout =  (SwipeLayout)findViewById(R.id.task_swipe_layout);
        mSwipeLayout.setFocusable(true);
        mSwipeLayout.setFocusableInTouchMode(true);

        //set show mode.
        mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)

        // TODO: 05.06.2018 РАСКОММЕНТИРОВАТЬ
        mSwipeLayout.addDrag(SwipeLayout.DragEdge.Left, findViewById(R.id.bottom_task_wrapper_right2));
        mSwipeLayout.addDrag(SwipeLayout.DragEdge.Right, findViewById(R.id.bottom_task_wrapper_right));
        mSwipeLayout.setLeftSwipeEnabled(true);
        mSwipeLayout.setRightSwipeEnabled(true);


        mSwipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
                if (/*mSwipeLayout.getDragEdge() != SwipeLayout.DragEdge.Left &&*/ canSwipeToRefresh) {
                    SlidingActivity.mSwipeRefreshLayout.setEnabled(TasksFragment.canSwipeToRefresh); // включаем SwipeRefresh если вы вверху списка
                }
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.

                if(layout.getDragEdge()== SwipeLayout.DragEdge.Left) {
                    if ((layout.getWidth()*0.1650f) > leftOffset) {
                        mLeftSlider.setBackgroundColor(getResources().getColor(R.color.unreaded_action_main));
                    } else {
                        mLeftSlider.setBackgroundColor(getResources().getColor(R.color.finish_color));
                    }
                } else {
                    if(layout.getDragEdge()== SwipeLayout.DragEdge.Right) {
                        if ((layout.getWidth() * -0.1650f) < leftOffset) {
                            mRightSlider.setBackgroundColor(getResources().getColor(R.color.unreaded_action_main));
                        } else {
                            mRightSlider.setBackgroundColor(getResources().getColor(R.color.profile_button_buy_color));
                        }
                    }
                }
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {
                SlidingActivity.mSwipeRefreshLayout.setEnabled(false); // отключаем SwipeRefresh
                TasksFragment.mTempTask = mTask;
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                if(layout.getDragEdge()== SwipeLayout.DragEdge.Right) {
                    onSwipe(LTaskItemView.this);
                } else {
                    deleteCell(LTaskItemView.this);
                    resetColorBackground();
                    closeSwipeWithoutOnTaskSwipe();
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

    private void deleteCell(final View v/*, final int index*/) {
        AnimationListener al = new AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                mListener.onClickTaskStatus(mTask, mIvStatus, false);
                /*mAnimList.remove(index);

                ViewHolder vh = (ViewHolder)v.getTag();
                vh.needInflate = true;

                mMyAnimListAdapter.notifyDataSetChanged();*/
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };

        collapse(v, al);
    }

    private void collapse(final View v, AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                }
                else {
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        if (al!=null) {
            anim.setAnimationListener(al);
        }
        anim.setDuration(ANIMATION_DURATION);
        v.startAnimation(anim);
    }

    private boolean isPerformerOrCustomerInTask(LTask task)
    {
        String userName = LTSettings.getInstance().getUserName();
        boolean isCustomer = userName.equals(task.getEmailCustomer());
        boolean isPerformer = userName.equals(task.getEmailPerformer());
        if(isCustomer || isPerformer) {
            return true;
        } else {
            return false;
        }
    }

    public void showTaskPropertiesDrawer(LTask task) {
        // Set up the drawer.
        BaseActivity mActivity = (BaseActivity) getContext();
        final FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.navigation_drawer_task, TaskDrawerFragment.newInstance(task), MenuFragment.CLASS_PATH);
        ft.commit();

        SlidingActivity.mTaskDrawerLayout.openDrawer(Gravity.RIGHT);
    }

    public void setData(Cursor cursor) {
        mTask.fillFromCursor(cursor);
        mPosition = cursor.getPosition();
        if(mTaskCache == null) {
            mTaskCache = LTaskCache.getInstance(getContext()); //добавлено ибо скорее всего потому что кэш не вызывается
        }
        mCacheHolder = mTaskCache.find(mTask.getIdTask());
        setBackgroundColorDefault();
        setData();

        if (isPerformerOrCustomerInTask(mTask) && !TasksFragment.isCheckModeOn) {
            mSwipeLayout.setEnabled(true);
            mSwipeLayout.setLeftSwipeEnabled(true);
            mSwipeLayout.setRightSwipeEnabled(true);
        } else {
            mSwipeLayout.setEnabled(false);
            mSwipeLayout.setLeftSwipeEnabled(false);
            mSwipeLayout.setRightSwipeEnabled(false);
        }
    }

    private void setData() {
        try {
            final boolean completed = isCompleted();
            final boolean termBeginEndExists = mTask.getTermBegin() != 0 && mTask.getTermEnd() != 0;
            Marker marker = null;

            if (mCacheHolder != null)
                marker = mCacheHolder.getMarker();

            setImageStatus(marker);
            setChildsCount();
            setName(completed);
            setTerm(termBeginEndExists);
            setFilesCount();
            setCommentsAndMessages();
            addCategories(showExpired(completed, termBeginEndExists), showChronometry(completed));

            mIvFocus.setVisibility(mTask.getFocus() ? View.VISIBLE : View.GONE);
        }catch(Throwable e){
            e.printStackTrace();
        }
    }

    private boolean isCompleted() {
        if(mCacheHolder == null) {
            return false;
        }

    	if(mCacheHolder.getCompletedTask()!= null)
    	{
            //Завершено, Отменено
            //и если я не заказчик, то готово к сдаче или отклонено
            final CompletedTask completed = mCacheHolder.getCompletedTask();
            if (completed != null && (completed.isParentCompleted() || completed.isTaskCompleted())) {
                // заказчик не я?
                if (!mSettings.getUserName().equals(mTask.getEmailPerformer()))
                {
                    // задачи у которых поставили готово к сдаче или отклонили
                    // видит только заказчик
                    if ( mTask.getStatus() == Status.TASK_READY.getStatusCode() || mTask.getStatus() == Status.TASK_REJECTED.getStatusCode()) {
                        return true;
                    }
                }
                return mTask.getStatus() == Status.TASK_COMPLETED.getStatusCode() || mTask.getStatus() == Status.TASK_CANCELLED.getStatusCode();
            }
	        return false;
    	}
    	else
    	{
    		return false;
    	}
    }

    public void setCheckedBackground() {
        final StateListDrawable selector = new StateListDrawable();
        selector.addState(STATE_PRESSED, new ColorDrawable(getResources().getColor(R.color.sliding_menu_background)));
        selector.addState(STATE_UNPRESSED, new ColorDrawable(getResources().getColor(R.color.properties_color_divider2)));

        mMainTaskContainer.setBackgroundDrawable(selector);

    }

    private void setImageStatus(Marker marker) {
        final boolean whiteImage = mSettings.isThemeDark() && (marker == null || marker.getBackColor() == null);
        final TaskStatus status = TaskStatus.getTaskStatus(mTask);

        if (mTask.getSeriesType() == SeriesType.NONE.ordinal()) {
            mIvStatus.setImageResource(whiteImage ? status.getResIdWhite() : status.getResId());

        } else {
            mIvStatus.setImageResource(whiteImage ? status.getSeriesWhiteResId() : status.getSeriesResId());
        }
    }

    private void setChildsCount() {
        if (mCacheHolder == null)
            return;

        try {
            final TaskTotalLink totalLink = mCacheHolder.getTaskTotal(); // mCacheHolder = mTaskCache.find(mTask.getIdTask());
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

    private void setName(boolean completed) {
        final boolean toUpperCase;
        final int typeface;

        if (mSettings.isStrikethruTask() && completed) {
            setCompletedTextColorDefault();

            mTvTaskName.setBackgroundColor(Color.TRANSPARENT);
            mTvTaskName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

            toUpperCase = false;
            typeface = Typeface.NORMAL;

        } else {
            setCompletedTextColorBlack();
            mTvTaskName.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
            toUpperCase = resetColorBackground();
            typeface = mTask.getReaded() ? Typeface.NORMAL : Typeface.BOLD;
        }

        setTypeface(mTvTaskName, typeface);
        for (TextView text : mModifiedViews) {
            setTypeface(text, typeface);
        }

        if (toUpperCase && mTask.getName() != null) {
            mTvTaskName.setText(mTask.getName().toUpperCase());

        } else {
            mTvTaskName.setText(mTask.getName());
        }
    }

    private static void setTypeface(TextView tv, int typeface) {
        tv.setTypeface(null, typeface);
    }

    private void setCompletedTextColorDefault() {
        mTvTaskName.setTextColor(mColorCompleted);
        for (TextView text : mModifiedViews) {
            text.setTextColor(mColorCompleted);
        }
    }

    private void setCompletedTextColorBlack() {
        mTvTaskName.setTextColor(0xff000000);
        for (TextView text : mModifiedViews) {
            text.setTextColor(0xff000000);
        }
    }

    private boolean setMarker(Marker marker) {
        if (marker == null) {
            setBackgroundColorDefault();
            setTextColorDefault();
            return false;

        } else {
            if (marker.getBackColor() == null || Marker.NO_COLOR.equals(marker.getBackColor())) {
                setBackgroundColorDefault();
            } else {
                final StateListDrawable selector = new StateListDrawable();
                selector.addState(STATE_PRESSED, new ColorDrawable(getResources().getColor(R.color.sliding_menu_background)));
                selector.addState(STATE_UNPRESSED, new ColorDrawable(parseColor(marker.getBackColor())));

                mMainTaskContainer.setBackgroundDrawable(selector);
            }

            if (marker.getTextColor() == null || Marker.NO_COLOR.equals(marker.getTextColor())) {
                setTextColorDefault();

            } else {
                final int colorText = parseColor(marker.getTextColor());
                mTvTaskName.setTextColor(colorText);
                for (TextView text : mModifiedViews) {
                    text.setTextColor(colorText);
                }
            }

            return marker.isUppercase();
        }
    }

    private int parseColor(String color) {
        int clr = -1;
        try {
            clr = Color.parseColor(color);
        } catch (Exception e) {
            clr = Color.BLACK;
        } finally {
            return clr;
        }
    }

    private void setBackgroundColorDefault() {
        final StateListDrawable selector = new StateListDrawable();
        selector.addState(STATE_PRESSED, new ColorDrawable(getResources().getColor(R.color.sliding_menu_background)));
        selector.addState(STATE_UNPRESSED, new ColorDrawable(getResources().getColor(R.color.white)));

        mMainTaskContainer.setBackgroundDrawable(selector);
    }

    private void setTextColorDefault() {
        final int color = mSettings.isThemeDark() ? Color.WHITE : Color.BLACK;
        mTvTaskName.setTextColor(color);
        for (TextView text : mModifiedViews) {
            text.setTextColor(color);
        }
    }

    private void setTerm(boolean termBeginEndExists) {
        if (termBeginEndExists) {
            setTerm(mTvTerm, true, true);
            if (mIvTerm.getVisibility() != View.VISIBLE) {
                mIvTerm.setVisibility(View.VISIBLE);
                mTvTerm.setVisibility(View.VISIBLE);
            }
            if (mIvTermCustomer.getVisibility() != View.GONE) {
                mIvTermCustomer.setVisibility(View.GONE);
                mTvTermCustomer.setVisibility(View.GONE);
            }

            setUserAndTerm(false, termBeginEndExists);

        } else {
            if (mIvTerm.getVisibility() != View.GONE) {
                mIvTerm.setVisibility(View.GONE);
                mTvTerm.setVisibility(View.GONE);
            }

            setUserAndTerm(true, termBeginEndExists);
        }
    }

    private void setTerm(TextView tv, boolean performer, boolean appendSeriesString) {
        Utils.clearStringBuilder(mStringBuilder);

        mStringBuilder.append(mTimeHelper.taskTermFormatter(mTask, performer, false));
        /*if (appendSeriesString) {
            TaskHelper.appendSeriesString(getContext(), mStringBuilder, mTask, true);
        }*/

        tv.setText(mStringBuilder);
    }

    private void setUserAndTerm(boolean setTerm, boolean termBeginEndExists) {
        mIvUserTwo.setVisibility(GONE);
        mIvUserCustomTwo.setVisibility(GONE);
        twoFotoContainer.setVisibility(GONE);

        if (mSettings.getUserName().equals(mTask.getEmailCustomer())) {
            if (mSettings.getUserName().equals(mTask.getEmailPerformer())) {
                if (mIvUser.getVisibility() != View.GONE) {
                    mIvUser.setVisibility(View.GONE);
                    mIvUserCustom.setVisibility(View.GONE);
                    mainFotoContainer.setVisibility(GONE);
                    twoFotoContainer.setVisibility(GONE);
                    //mTvUser.setVisibility(View.GONE);
                }

            } else {
                //mTvUser.setText(mEmployeeCache.find(mTask.getEmailPerformer()));
                if(mTask.getPerformerReaded()) {
                    //mIvUser.setImageResource(R.drawable.tome_task);
                    setPerformerFoto(mIvUser, mIvUserCustom, mTask.getEmailPerformer(), true, 0);
                }
                else {
                    //mIvUser.setImageResource(R.drawable.performer_not_read_list);
                    setPerformerFoto(mIvUser, mIvUserCustom, mTask.getEmailPerformer(), false, 0);
                }

                if (mIvUser.getVisibility() != View.VISIBLE) {
                    mIvUser.setVisibility(View.VISIBLE);
                    mainFotoContainer.setVisibility(VISIBLE);
                    //mTvUser.setVisibility(View.VISIBLE);
                }
            }

            if (setTerm && mIvTermCustomer.getVisibility() != View.GONE) {
                mIvTermCustomer.setVisibility(View.GONE);
                mTvTermCustomer.setVisibility(View.GONE);
            }

        } else {
            //if (setTerm) {
                if (mTask.getTermBeginCustomer() != 0 && mTask.getTermEndCustomer() != 0) {
                    setTerm(mTvTermCustomer, false, !termBeginEndExists);
                    if (mIvTermCustomer.getVisibility() != View.VISIBLE) {
                        mIvTermCustomer.setVisibility(View.VISIBLE);
                        mTvTermCustomer.setVisibility(View.VISIBLE);
                    }

                } else if (mIvTermCustomer.getVisibility() != View.GONE) {
                    mIvTermCustomer.setVisibility(View.GONE);
                    mTvTermCustomer.setVisibility(View.GONE);
                }
            //}

            if (mSettings.getUserName().equals(mTask.getEmailPerformer())) {
                setPerformerFoto(mIvUser, mIvUserCustom, mTask.getEmailCustomer(), false, 1);
            } else {

                if (!mTask.getEmailCustomer().equals(mTask.getEmailPerformer())) {
                    mIvUserCustomTwo.setVisibility(VISIBLE);
                    mIvUserTwo.setVisibility(VISIBLE);
                    twoFotoContainer.setVisibility(View.VISIBLE);
                    setPerformerFoto(mIvUserTwo, mIvUserCustomTwo, mTask.getEmailPerformer(), false, 2);
                }

                setPerformerFoto(mIvUser, mIvUserCustom, mTask.getEmailCustomer(), false, 2);
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

    private void setCommentsAndMessages() {
        Utils.changeVisibility(mIvTaskComment, TextUtils.isEmpty(mTask.getComment()) ? View.GONE : View.VISIBLE);
        if (mCacheHolder != null) {
            Utils.changeVisibility(mIvMessages, mCacheHolder.isHasMessages() ? View.VISIBLE : View.GONE);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void addCategories(boolean showExpired, boolean showChronometry) {
        final String[] uids = TaskHelper.getCategoriesFromString(mTask.getCategories());

        final boolean hasCategories = uids.length > 0;
        mCategories.removeAllViews();
        Utils.changeVisibility(mCategories, View.VISIBLE);

        if (showExpired) {
            mCategories.addView(inflate(getContext(), R.layout.view_expired, null));
        }

        timerHandler.removeCallbacks(timerRunnable);
        int min = mTask.getPlan();
        String text = "";
        int wasInWork = mTask.getTime()+(int) ((TimeHelper.getInstance().currentTimeMillisWithoutTimeZone() - mTask.getInWorkTime()) / 1000); // сек

        if (mSettings.isShowChrono()) {
            if (showChronometry) {
                View v = inflate(getContext(), R.layout.view_chronometry, null);
                mTimeTextView = (TextView) v.findViewById(R.id.text_chrono);

                if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                    //int wasInWork = mTask.getTime(); // сек
                    if (mTask.getInWorkTime() == 0) {
                        wasInWork = 0;
                    }
                    text = Utils.getTextInWork(getContext(), wasInWork, min, mTask.getStatus());
                    timerHandler.postDelayed(timerRunnable, 1000);

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                } else {
                    wasInWork = mTask.getTime();
                    text = Utils.getTextInWork(getContext(), wasInWork, min, mTask.getStatus());
                }

                mTimeTextView.setText(text);
                mCategories.addView(v);
            } else {
                View v = inflate(getContext(), R.layout.view_chronometry, null);
                mTimeTextView = (TextView) v.findViewById(R.id.text_chrono);
                if (mTask.getStatus() == Status.TASK_IN_WORK.getStatusCode()) {
                    mTimeTextView = (TextView) v.findViewById(R.id.text_chrono);
                    text = Utils.getTextInWork(getContext(), wasInWork, min, mTask.getStatus());

                    mTimeTextView.setText(text);
                    mCategories.addView(v);
                    timerHandler.postDelayed(timerRunnable, 1000);
                }
            }
        }

        if (hasCategories) {
            List<Category> categories = new ArrayList<>();
            try {
                List<ITreePureNode> allCategories =  getListAllCategories(DbHelper.getInstance(getContext()));
                for (ITreePureNode categoryITree : allCategories) {
                    for (int i = 0; i < uids.length; i++) {
                        if(uids[i].toLowerCase().equals(((Category)categoryITree).getId().toString().toLowerCase())) {
                            categories.add((Category)categoryITree);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (Category category : categories) {
                final TextView v = (TextView) inflate(getContext(), R.layout.view_flow_item_label, null);

                v.setText(category.getName());
                boolean wasException = false;
                final Drawable d = getResources().getDrawable(R.drawable.label);
                if (category.getColor() != null && !category.getColor().equals(Category.NO_COLOR)) {
                    try {
                        final int color = parseColor(category.getColor());
                        d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        v.setTextColor(Utils.whatColorToUse(color));

                    } catch (Exception e) {
                        wasException = true;
                    }
                    if (!wasException) {
                        v.setBackgroundDrawable(d);
                        mCategories.addView(v);
                    }
                } else {
                    try {
                        final int color = parseColor("#808080");
                        d.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
                        v.setTextColor(Utils.whatColorToUse(color));

                    } catch (Exception e) {
                        wasException = true;
                    }
                    if (!wasException) {
                        v.setBackgroundDrawable(d);
                        mCategories.addView(v);
                    }
                }

            }

        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public List<ITreePureNode> getListAllCategories(DbHelper dbHelper) {
        final List<ITreePureNode> data = new ArrayList<ITreePureNode>();

        final List<Category> categories;
        try {
            categories = dbHelper.getCategoryDao().queryForAll();
        } catch (SQLException e) {
            return data;
        }
        Collections.sort(categories);

        for (Category c : categories) {
            if (c.getParentId() == null /*&& LTSettings.getInstance().getUserName().equals(c.getCreator())*/ ) {
                data.add(c);
                processListSubCategories(data, c, categories);
            }
        }

        return data;
    }

    private void processListSubCategories(List<ITreePureNode> data, Category parent, List<Category> categories) {
        for (Category c : categories) {
            if (parent.getId().equals(c.getParentId())) {
                data.add(c);
                parent.addChild(c);
                processListSubCategories(data, c, categories);
            }
        }
    }

    private boolean showExpired(boolean completed, boolean termBeginEndExists) {
        if (!completed) {
            TimeHelper.roundCalendar(mCalendar, true);

            if (termBeginEndExists) {
                return mTask.getTermEnd() < mCalendar.getTimeInMillis();

            } else if (mSettings.getUserName().equals(mTask.getEmailPerformer()) && mTask.getTermEndCustomer() != 0) {
                return mTask.getTermEndCustomer() < mCalendar.getTimeInMillis();
            }
        }
        return false;
    }

    private boolean showChronometry(boolean completed) {
        //if (!completed) {
            TimeHelper.roundCalendar(mCalendar, true);

            if (mTask.getTime() != 0 || mTask.getPlan() != 0) {
                return true;
            }
        //}
        return false;
    }

    private boolean resetColorBackground() {
        boolean result = false;
        Marker marker = null;

        if (mCacheHolder != null)
            marker = mCacheHolder.getMarker();

        if (TasksFragment.isCheckModeOn) {
            if (mSettings.getCheckedTasks().contains(mTask.getUid())) {
                setCheckedBackground();
                return false;
            } else {
                result = setMarker(marker);
            }
        }

        result = setMarker(marker);

        return result;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_task_status /*&& mSettings.getStatusBehavior() != TaskStatusBehavior.NONE*/) {
            mListener.onClickTaskStatus(mTask, mIvStatus, true);

        } else {
            mListener.onClickTask(mTask, mHasChilds, mPosition);
            resetColorBackground();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        mListener.onLongClickTask(mTask, v, false);
        resetColorBackground();
        closeAllSwipeViews();
        return true;
    }

    public void onSwipe (View v) {
        mListener.onLongClickTask(mTask, v, true);
        resetColorBackground();
        closeAllSwipeViews();
    }

    private void closeAllSwipeViews() {
        mSwipeLayout.close();
        SlidingActivity.mSwipeRefreshLayout.setEnabled(true); // включаем SwipeRefresh
        SlidingActivity.mTaskDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // нет свайпа
    }

    private void closeSwipeWithoutOnTaskSwipe() {
        SlidingActivity.mSwipeRefreshLayout.setEnabled(true); // включаем SwipeRefresh
        SlidingActivity.mTaskDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED); // нет свайпа
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