package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNpl;

import android.widget.Spinner;

public class WarehouseSetting3_62 extends WarehouseSetting {
	Spinner spLinesCount;
	
	@Override
	protected void init() {
		super.init();
		
		spLinesCount = (Spinner) findViewById(R.id.spLinesCount);
		
		CfgNpl cfex = (CfgNpl)config;
		
		if (cfex.linesCount > 0 && cfex.linesCount <= spLinesCount.getCount())
			spLinesCount.setSelection(cfex.linesCount - 1, true);
	}
	
	@Override
	public void save() {
		CfgNpl cfex = (CfgNpl)config;
		cfex.linesCount = spLinesCount.getSelectedItemPosition() + 1;
		super.save();
	}
}
