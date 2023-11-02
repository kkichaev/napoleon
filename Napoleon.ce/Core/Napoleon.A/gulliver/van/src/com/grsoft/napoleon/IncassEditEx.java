package com.grsoft.napoleon;

import com.grsoft.dataobjects.IncassEx;

import android.os.Bundle;
import android.widget.CheckBox;

public class IncassEditEx extends IncassEdit {
	private CheckBox cbGenData;
	
	@Override
	protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		cbGenData = (CheckBox) findViewById(R.id.cbGenDog);
		
		IncassEx iex = (IncassEx) doc.getData();
		cbGenData.setChecked(iex.gendog == 1);
	}
	
	@Override
	protected void save() {
		IncassEx iex = (IncassEx) doc.getData();
		iex.gendog = cbGenData.isChecked() ? 1 : 0;
		super.save();
	}
}
