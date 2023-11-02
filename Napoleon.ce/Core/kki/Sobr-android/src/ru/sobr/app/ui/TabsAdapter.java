package ru.sobr.app.ui;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

public class TabsAdapter extends FragmentStatePagerAdapter implements
        TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {

    //public static final String TAG = "TabsAdapter";
    //private static final boolean DEBUG = false;

    private final Context mContext;
    private final TabHost mTabHost;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    private boolean dataChenged = false;

    static final class TabInfo {
        //private final String tag;
        private final Class<?> clss;
        private final Bundle args;

        TabInfo(String _tag, Class<?> _class, Bundle _args) {
            //tag = _tag;
            clss = _class;
            args = _args;
        }
    }

    static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            mContext = context;
        }

        @Override
        public View createTabContent(String tag) {
            View v = new View(mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public TabsAdapter(FragmentActivity activity, TabHost tabHost,
                       ViewPager pager) {
        super(activity.getSupportFragmentManager());
        mContext = activity;
        mTabHost = tabHost;
        mViewPager = pager;
        mTabHost.setOnTabChangedListener(this);
        mViewPager.setAdapter(this);
        mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
        tabSpec.setContent(new DummyTabFactory(mContext));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);
        mTabs.add(info);
        //if(DEBUG) if(tabSpec != null) Log.d(TAG, "tabSpec != null");
        //if(DEBUG) if(mTabHost != null) Log.d(TAG, "mTabHost != null");
        mTabHost.addTab(tabSpec);
        notifyDataSetChanged();
    }

    public void deleteTab(int position) {
        mTabs.remove(position);
        mTabHost.getTabWidget().removeView(mTabHost.getTabWidget().getChildTabViewAt(position));
        notifyDataSetChanged();
    }

    public void deleteAllTabs() {
        //if(DEBUG)Log.d(TAG, "deleteAllTabs");
        mTabs.clear();
        mTabHost.clearAllTabs();
        notifyDataSetChanged();
        mTabHost.setup();
    }

    public boolean notifyDataSetChangedCalled() {
        boolean res = dataChenged;
        if (dataChenged) dataChenged = false;

        return res;
    }

    @Override
    public void notifyDataSetChanged() {
        dataChenged = true;
        super.notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(View container, int position) {
        //if(DEBUG)Log.d(TAG, "instantiateItem");
        return POSITION_NONE;
    }


    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public int getItemPosition(Object object) {
        //if(DEBUG)Log.d(TAG, "getItemPosition");
        return super.getItemPosition(object);
    }

    @Override
    public Fragment getItem(int position) {
        TabInfo info = mTabs.get(position);
        return Fragment.instantiate(mContext, info.clss.getName(), info.args);
    }

    @Override
    public void onTabChanged(String tabId) {
        //if(DEBUG) Log.d(TAG, "onTabChanged");
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        TabWidget widget = mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
