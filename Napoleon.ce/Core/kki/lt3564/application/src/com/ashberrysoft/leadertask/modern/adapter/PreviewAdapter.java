package com.ashberrysoft.leadertask.modern.adapter;
import java.lang.ref.WeakReference;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.text.Html;

import com.ashberrysoft.leadertask.modern.activity.PreviewActivity;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment2;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment3;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment4;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment5;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment6;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragment7;
import com.ashberrysoft.leadertask.modern.fragment.intro_fragments.PreviewFragmentPhone;
import com.ashberrysoft.leadertask.utils.SharedStrings;

public class PreviewAdapter extends FragmentStatePagerAdapter  {
    // BASE
    private final WeakReference<PreviewActivity> mActivity;

    // VALUE's
    private final int mCount;

    public PreviewAdapter(PreviewActivity activity) {
        super(activity.getSupportFragmentManager());

        mActivity = new WeakReference<PreviewActivity>(activity);
        mCount = 8;
    }

    @Override
    public Fragment getItem(int position) {
        final PreviewActivity activity = mActivity.get();
        if (activity != null) {
            switch (position) {
                case 0:
                    return PreviewFragment.newInstance();
                case 1:
                    return PreviewFragment2.newInstance();
                case 2:
                    return PreviewFragment3.newInstance();
                case 3:
                    return PreviewFragmentPhone.newInstance(); // new
                case 4:
                    return PreviewFragment4.newInstance();
                case 5:
                    return PreviewFragment5.newInstance();
                case 6:
                    return PreviewFragment6.newInstance();
                case 7:
                    return PreviewFragment7.newInstance();
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        final PreviewActivity activity = mActivity.get();
        if (activity != null) {
            return Html.fromHtml(SharedStrings.CIRCLE);
        }
        return super.getPageTitle(position);
    }
}