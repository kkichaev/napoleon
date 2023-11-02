package com.grsoft.napoleon;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class SilentEditText extends EditText {

	public SilentEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public SilentEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SilentEditText(Context context) {
		super(context);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}
}
