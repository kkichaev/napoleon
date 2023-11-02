package com.grsoft.napoleon;

import android.widget.CheckBox;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void postSync(Boolean result) {
		if (result && ((CheckBox)findViewById(R.id.cbDebt)).isChecked()){
			NapoleonEx.loadedDebs = false;
		}
	}
}
