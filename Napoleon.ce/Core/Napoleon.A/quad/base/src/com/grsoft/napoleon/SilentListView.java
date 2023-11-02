package com.grsoft.napoleon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.ListView;

public class SilentListView extends ListView {

	public SilentListView(Context context) {
		super(context);
	}
	
	public SilentListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public SilentListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
