package com.grsoft.ads.view;

import com.grsoft.ads.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.Button;

public class MyButton extends Button {
	Paint paint = new Paint(); 
	
	public MyButton(Context context) {
		super(context);
	}
	
	public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	paint.setStrokeWidth(2);
    	paint.setStyle(Style.STROKE);
    	paint.setColor(getContext().getResources().getColor(R.color.button_border));
    	
    	Rect rect = new Rect(0,0,getWidth(), getHeight());
    	canvas.drawRect(rect, paint);
    }

}
