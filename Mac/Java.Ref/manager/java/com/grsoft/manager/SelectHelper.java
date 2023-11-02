package com.grsoft.manager;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.widget.TextView;

public class SelectHelper implements OnClickListener  {
	private View control;
	
	public void setControl(TextView view){ this.control = view; }
	
	public View getControl(){ return control; }
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		applySelect(which);
		dialog.dismiss();
	}
	
	protected void applySelect(int which){};
}
