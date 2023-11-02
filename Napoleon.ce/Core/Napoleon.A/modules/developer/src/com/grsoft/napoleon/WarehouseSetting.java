package com.grsoft.napoleon;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;
import com.grsoft.napoleon.util.CfgNpl;


public class WarehouseSetting extends WarehouseSettingW {
	private CheckBox cbPriceImage;
	private Spinner spPriceImagePos;
	private CheckBox cbShowDailySales;
	
	@Override protected int getContentViewID() { return R.layout.warehouse_setting_new;	}
	@Override
	protected void init() {
		super.init();
		cbPriceImage = (CheckBox) findViewById(R.id.cbPriceImage);
		spPriceImagePos = (Spinner) findViewById(R.id.spPriceImagePos);
		cbShowDailySales = (CheckBox) findViewById(R.id.cbShowDailySales);
		
		CfgNpl cfex = (CfgNpl)config;
		
		if(cbPriceImage != null){
			cbPriceImage.setOnCheckedChangeListener(onCheckPriceImage());
			cbPriceImage.setChecked(cfex.showImageInPriceCount);
		}
		
		if(spPriceImagePos != null)
			spPriceImagePos.setSelection(cfex.imagePosInPriceCount, true);
		
		if(cbShowDailySales != null)
			cbShowDailySales.setChecked(cfex.showDailySales);
	}
	
	@Override
	protected void initUpdatePriceControls() {}
	
	private OnCheckedChangeListener onCheckPriceImage() {
		return new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(spPriceImagePos != null)
					spPriceImagePos.setEnabled(isChecked);
			}
		};
	}
	
	@Override
	public void save() {
		CfgNpl cfex = (CfgNpl)config;
		
		if(cbPriceImage != null)
			cfex.showImageInPriceCount = cbPriceImage.isChecked();
		
		if(spPriceImagePos != null)
			cfex.imagePosInPriceCount = spPriceImagePos.getSelectedItemPosition();
		
		if(cbShowDailySales != null)
			cfex.showDailySales = cbShowDailySales.isChecked();
		
		super.save();
	}
}
