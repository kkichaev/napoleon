package com.grsoft.ads.view;

import com.grsoft.ads.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.TextView;


public class TextViewW extends TextView {
	private boolean underline = false;
	
	public TextViewW(Context context){
		super(context);
	}
	
	public TextViewW(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public TextViewW(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}
	
	private void init(Context ctx, AttributeSet attrs){
		TypedArray a = ctx.getTheme().obtainStyledAttributes(attrs, R.styleable.TextViewW, 0, 0);
		
		try{
			underline = a.getBoolean(R.styleable.TextViewW_text_underline, false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Layout l = getLayout();
		
		if(l != null){
			Paint p = l.getPaint();
			
			if(p != null)
				p.setUnderlineText(underline);
		}
		
		super.onDraw(canvas);
	}

}
