package com.grsoft.napoleon;

import java.util.Date;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		((CheckBox) findViewById(R.id.cbRemains)).setVisibility(View.INVISIBLE);
	}
	
	@Override
	protected void postSync(Boolean result) {
		if(result) {
			SharedPreferences sp = getSharedPreferences(DocumentsEx.LAST_SYNC, MODE_PRIVATE);
			Date d = new Date();
			SharedPreferences.Editor e = sp.edit();
			e.putLong(DocumentsEx.LAST_SYNC, d.getTime());
			e.commit();
		}
	}
}
