package com.grsoft.ads.utils;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.grsoft.napoleon.util.LinesCountController;

public abstract class ItemsBaseAdapter extends BaseAdapter {
	private LinesCountController controller;
	private Context context;
	
	public ItemsBaseAdapter(Context context, LinesCountController controller){
		this.controller = controller;
		this.context = context;
	}
	
	protected void applyLineController(TextView textView){
		controller.prepareTextView(textView);
	}
	
	public Context getContext() {
		return context;
	}
}
