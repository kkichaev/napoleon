package com.ksoft.anotherworld.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ksoft.anotherworld.R;

public class GameModeControl extends LinearLayout implements OnClickListener {
	FiredButton btnHero;
	TextView tvCaption;
	FiredButton btnMove;
	FiredButton btnChat;
	Button btnDummy;
	
	public interface OnSelectMode{
		void selectMode(int idx);
	}
	
	public OnSelectMode onSelectMode;
	
	@SuppressWarnings("deprecation")
	public GameModeControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		btnHero = new FiredButton(context, attrs);
		btnHero.setActive(true);
		btnHero.setTag("Персонаж");
		btnHero.setOnClickListener(this);
		btnHero.setBackgroundDrawable (context.getResources().getDrawable(R.drawable.herobkg));
		btnMove = new FiredButton(context, attrs);
		btnMove.setTag("Навигация");
		btnMove.setOnClickListener(this);
		btnMove.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.movebkg));
		btnChat = new FiredButton(context, attrs);
		btnChat.setBackgroundDrawable(getResources().getDrawable(R.drawable.chatbkg));
		btnChat.setTag("Чат");
		btnChat.setOnClickListener(this);
		btnDummy = new Button(context, attrs);
		btnDummy.setBackgroundDrawable(getResources().getDrawable(R.drawable.herobkg));
		tvCaption = new TextView(context, attrs);
		Typeface face = Typeface.createFromAsset(context.getAssets(),
				"fonts/A_Stamper.ttf");

		tvCaption.setTypeface(face);
		tvCaption.setText("Персонаж");
		tvCaption.setTextSize(22);
		
		
		addView(btnHero,0,params);
		
		addView(btnMove, 1, params);
		addView(btnChat, 2, params);
		addView(btnDummy, 3, params);
		
		params.setMargins(5, 0, 5, 0);
		addView(tvCaption, 1, params);
	}

	@Override
	public void onClick(View v) {
		for(int i = 0; i < getChildCount(); i++){
			View child = getChildAt(i);
			
			if(child instanceof FiredButton && child != v){
				((FiredButton) child).setActive(false);
			}
		}
		
		if (v instanceof FiredButton){
			removeView(tvCaption);
			
			
			((FiredButton) v).setActive(true);
			int idx = indexOfChild(v);
			
			if(onSelectMode != null)
				onSelectMode.selectMode(idx);
				
			LinearLayout.LayoutParams params = (LayoutParams) tvCaption.getLayoutParams();
			addView(tvCaption, idx + 1, params);
			tvCaption.setText(v.getTag().toString());
			
			
		}
		
	}

}
