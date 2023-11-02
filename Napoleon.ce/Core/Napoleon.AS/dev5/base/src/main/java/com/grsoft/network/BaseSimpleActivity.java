package com.grsoft.network;
import com.grsoft.aceteam.R;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;


public abstract class BaseSimpleActivity extends FragmentActivity {
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
