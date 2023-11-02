package com.grsoft.ads;

import java.util.Calendar;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class TaskPageAdapter extends FragmentStatePagerAdapter{
	Fragment data[] = new Fragment[FRAGMENT_CNT];  
	private static final int FRAGMENT_CNT = 30;
	private static final int INVALID_INDEX = -1;

	public TaskPageAdapter(FragmentManager fm) {
		super(fm);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		cal.add(Calendar.DAY_OF_MONTH, -FRAGMENT_CNT/2);
		
		for (int i = 0; i < FRAGMENT_CNT; i++){
			data[i] = createFragment(cal.getTime());
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
	}
	
	public int findIndex(Date time) {
		int result = INVALID_INDEX;
		
		for(int i = 0; i < data.length; i ++)
			if (isSameData(time, ((AdsFragment)data[i]).date)){
				result = i;
				break;
			}
		
		return result;
	}

	public static boolean isSameData(Date d1, Date d2) {
		final int HOUR_PART = 1000 * 60 * 60 * 60 * 24;
		return (d1.getTime() / HOUR_PART) == (d2.getTime() / HOUR_PART);
	}

	private Fragment createFragment(Date val){
		AdsFragment result = new AdsFragment();
		
		if(val != null){
			Bundle args = new Bundle();
			args.putLong(AdsFragment.DATE, val.getTime());
			result.setArguments(args);
		}
		
		return result;
	}

	@Override
	public Fragment getItem(int index) { return data[index]; }

	@Override
	public int getCount() {	return data.length;	}
}