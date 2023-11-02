package com.ksoft.ksoftlib.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(getLayoutID());
		inflateView();
		init();
		initView();
		
	}
	
	private void initView() {}

	private void init() {}

	private void inflateView() {}

	public abstract int getLayoutID();
}
