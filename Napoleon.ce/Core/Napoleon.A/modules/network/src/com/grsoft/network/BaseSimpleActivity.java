package com.grsoft.network;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


public abstract class BaseSimpleActivity extends Activity {
	protected abstract int getLayoutID();
	protected void inflateView(){};
	protected void init(){};
	protected void initView(){};
	
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(getLayoutID());
		inflateView();
		init();
		initView();
	};
	
	public Context getContext(){ return this; }
}
