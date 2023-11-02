package com.ashberrysoft.leadertask.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;

import com.ashberrysoft.leadertask.R;
import com.ashberrysoft.leadertask.domains.ordinary.Category;
import com.ashberrysoft.leadertask.domains.ordinary.Task;
import com.ashberrysoft.leadertask.fragments.TaskCommentsFragment;
import com.ashberrysoft.leadertask.fragments.TaskEditFragment;
import com.ashberrysoft.leadertask.utils.Utils;

/**
 * Адаптер, предназначенный для формирования фрагментов, которые будут находиться во ViewPager (экран свойств задачи)
 * 
 * @author Vadim Oleynik (vadim.welldone@gmail.com)
 * 
 */
public class CustomFragmentStatePagerAdapter extends FragmentStatePagerAdapter {

    private static final int MAX_ITEMS_COUNT = 2;
    private static final int MIN_ITEMS_COUNT = 1;

    // BASE
    private final Context mContext;
    private final Task mTask;
    private final Category mCategory;
    private final View mView;
    private final boolean mNewTask;
    private final int mItemsCount;

    // VALUE
    private boolean mPaused;

    public CustomFragmentStatePagerAdapter(FragmentManager fm, Context context, Task task, Category category, View v,
            boolean newTask) {
        super(fm);

        mContext = context.getApplicationContext();
        mTask = task;
        mCategory = category;
        mView = v;
        mNewTask = newTask;
        mItemsCount = newTask ? MIN_ITEMS_COUNT : MAX_ITEMS_COUNT;

        mPaused = false;
    }

    @Override
    public int getItemPosition(Object object) {
        return mPaused ? PagerAdapter.POSITION_NONE : super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0:
            if (!mNewTask) {
                return TaskCommentsFragment.newInstance(mTask);
            }

        default:
            return TaskEditFragment.newInstance(mTask, mCategory, mNewTask);
        }
    }

    @Override
    public int getCount() {
        return mPaused ? 0 : mItemsCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Utils.hideInput(mContext, mView);

        switch (position) {
        case 0:
            if (!mNewTask) {
                return mContext.getString(R.string.title_message) + " (" + mTask.getMessagesCount() + ")";
            }

        default:
            return mContext.getString(R.string.title_edit);
        }
    }

    /**
     * Adapter is paused and should free all fragments.
     */
    public void setAdapterIsPaused() {
        mPaused = true;
    }

    /**
     * Adapter is resumed and can retain ll fragments.
     */
    public void setAdapterIsResumed() {
        mPaused = false;
    }
}