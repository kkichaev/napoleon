package com.grsoft.ads;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseDialogFragment extends DialogFragment {
	protected static final String TASKID = "taskid";
	protected abstract int getLayoutID();
	protected abstract void inflateView(View view);
	protected abstract void init();
	protected abstract void initView();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(getLayoutID(), null, false);
		inflateView(result);
		init();
		initView();
		return result;
	}
}
