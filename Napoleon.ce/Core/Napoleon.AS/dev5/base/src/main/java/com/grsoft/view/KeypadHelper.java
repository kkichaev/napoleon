package com.grsoft.view;
import com.grsoft.aceteam.R;

import com.grsoft.napoleon.Features;
import com.grsoft.aceteam.R;
import com.grsoft.util.OnClickListenerToNotify;

import android.app.Activity;
import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class KeypadHelper {
	int targetID;
	Finder a;
	View root = null;
	EditText targetView;
	
	interface Finder {
		View findViewById(int id);
	}
	
	class ActivityFinder implements Finder {
		Activity a;
		
		public ActivityFinder(Activity a) { this.a = a; }
		@Override public View findViewById(int id) { return a.findViewById(id); }
	}
	
	class ViewFinder implements Finder {
		View a;
		
		public ViewFinder(View a) { this.a = a; }
		@Override public View findViewById(int id) { return a.findViewById(id); }
	}

	public KeypadHelper(Activity a, int targetID) {
		this(a, targetID, Features.INTEGER_INPUTS_QTY);
	}
	
	public KeypadHelper(View v, int targetID) {
		this.a = new ViewFinder(v);
		this.targetID = targetID;
		
		makeKeypad(Features.INTEGER_INPUTS_QTY);
	}

	public KeypadHelper(Activity a, int targetID, boolean hideComma) {
		this.a = new ActivityFinder(a);
		this.targetID = targetID;
		
		makeKeypad(hideComma);
	} 
	
	public void setTargetID(int newID) {
		targetID = newID;
		targetView = null;
	}

	public void setTargetView(EditText v) {
		targetView = v;
	}
	
	public int getTargetID() { return targetID; }

	private void makeKeypad(boolean hideComma) {
		int keys[] = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
				R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnDel, R.id.btnComma};
		
		OnClickListener numKeyPress = new OnClickListenerToNotify() {
			
			@Override
			public void onClick(View v) {
				EditText ed = targetView != null ? targetView : (EditText) a.findViewById(targetID);
				if(ed == null)
					return;
				
				super.onClick(v);
				int s = ed.getSelectionStart();
				int e = ed.getSelectionEnd();
				Editable editable = ed.getText();

				if(v.getId() == R.id.btnComma && editable.toString().indexOf((String)v.getTag()) != -1)
					return;
				if (v.getId() == R.id.btnDel) {
					if( e < 0 ) e = editable.length();
					if( s < 0 || s == e ) s = e - 1;
					if (e > 0)
						editable.delete(s, e);
				} else {
					if( e < 0 ) {
						e = editable.length();
						s = e;
					}
					editable.replace(s, e, (String)v.getTag());
					ed.setSelection(editable.length());
				}
				
			}
		};
		
		if( hideComma ) {
			View v = a.findViewById(R.id.btnComma);
			if( v != null)
				v.setVisibility(View.INVISIBLE);
		}
		
		for (int resourceId: keys) {
			View v = a.findViewById(resourceId); 
			if( v != null ) {
				v.setOnClickListener(numKeyPress);
				v.setFocusable(false);
			}
		}
		
		ImageButton btnDele = (ImageButton) a.findViewById(R.id.btnDel);
		btnDele.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				((EditText) a.findViewById(targetID)).setText("");
				return false;
			}
		});
		btnDele.setFocusable(false);
	}
}
