package com.ksoft.anotherworld.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.ksoft.anotherworld.R;

public class FiredButton extends Button{
	private boolean active = false;
	
	private static final int[] PRESSED_STATE_SET = {
	        R.attr.state_fired
	   };

	public FiredButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FiredButton(Context context) {
		super(context);
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 
				PRESSED_STATE_SET.length);
        
		if (active) 
            mergeDrawableStates(drawableState, PRESSED_STATE_SET);
		
        return drawableState;
	}

	@Override
	public boolean performClick() {
		if(active)
			return true;
		else{
			active = true;
			return super.performClick();
		}
	}

	public void setActive(boolean val){
		active = val;
		refreshDrawableState();
	}
}
