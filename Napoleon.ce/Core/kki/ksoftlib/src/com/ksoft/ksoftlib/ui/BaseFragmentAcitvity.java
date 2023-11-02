package com.ksoft.ksoftlib.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class BaseFragmentAcitvity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(getLayoutID());
		inflateView();
		init();
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		
	}

	private void init() {
		// TODO Auto-generated method stub
		
	}

	private void inflateView() {
		// TODO Auto-generated method stub
		
	}

	public abstract int getLayoutID();
}
