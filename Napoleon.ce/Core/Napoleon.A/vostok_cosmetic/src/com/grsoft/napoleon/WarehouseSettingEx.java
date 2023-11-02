package com.grsoft.napoleon;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigImplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.AssortmentMatrixAdapter;

public class WarehouseSettingEx extends WarehouseSetting {

	private ConfigImpl cfgImpl;
	private Spinner spAssortRange;

	@Override protected int getContentViewID() {  return R.layout.warehouse_settingex; }
	
	@Override
	protected void init() {
		super.init();
		
//		findViewById(R.id.trColumn2).setVisibility(View.GONE);
//		findViewById(R.id.trColumn3).setVisibility(View.GONE);
		
		if( config instanceof ConfigImplEx )
		{
			ConfigImplEx ce = (ConfigImplEx)config;
			((CheckBox)findViewById(R.id.cbRestInAutoOrder)).setChecked(ce.useRestInAutoOrder);
			((EditText)findViewById(R.id.edAODays)).setText(Integer.toString(ce.daysForAutoorder));
		
			CheckBox cb;
			
			cb = (CheckBox) findViewById(R.id.cbInclude);
			cb.setChecked((config.priceClmn2Type & 1) != 0);
	
			cb = (CheckBox) findViewById(R.id.cbRest);
			cb.setChecked((config.priceClmn2Type & 2) != 0);

			cb = (CheckBox) findViewById(R.id.cbDocRest);
			cb.setChecked((config.priceClmn2Type & 4) != 0);
			
			cb = (CheckBox) findViewById(R.id.cbCost);
			cb.setChecked((config.priceClmn3Type & 1) != 0);
			
			cb = (CheckBox) findViewById(R.id.cbOrder);
			cb.setChecked((config.priceClmn3Type & 2) != 0);		
			
			spAssortRange = (Spinner) findViewById(R.id.spAssortRange);
			cfgImpl = new ConfigImpl();
			StringBuilder value = new StringBuilder();
			
			if(cfgImpl.getValue(value, "Usrrng") && Boolean.parseBoolean(value.toString()))
				spAssortRange.setEnabled(true);
			else
				spAssortRange.setEnabled(false);
			
			spAssortRange.setSelection(AssortmentMatrixAdapter.PERIOD_IN_MONTH - 1, true);
				
		}
	}
	
	@Override
	public void save() {
		if( config instanceof ConfigImplEx )
		{
			ConfigImplEx ce = (ConfigImplEx)config;
			ce.useRestInAutoOrder = ((CheckBox)findViewById(R.id.cbRestInAutoOrder)).isChecked();
			try {
				ce.daysForAutoorder = Integer.parseInt(((EditText)findViewById(R.id.edAODays)).getText().toString());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			
			StringBuilder value = new StringBuilder();
			if(cfgImpl.getValue(value, "Usrrng") && Boolean.parseBoolean(value.toString()))
			{
				AssortmentMatrixAdapter.PERIOD_IN_MONTH = spAssortRange.getSelectedItemPosition() + 1;
				
				ConfigImpl c = new ConfigImpl();
				c.getData().key = "Range";
				c.getData().value = Integer.toString(AssortmentMatrixAdapter.PERIOD_IN_MONTH);
				c.write();
				c.close();
			}
		}		
		
		super.save();
	
		CheckBox cb;
		int column2 = 0, column3 = 0;
		
		cb = (CheckBox) findViewById(R.id.cbInclude);
		if( cb.isChecked() )
			column2 |= 1;

		cb = (CheckBox) findViewById(R.id.cbRest);
		if( cb.isChecked() )
			column2 |= 2;

		cb = (CheckBox) findViewById(R.id.cbDocRest);
		if( cb.isChecked() )
			column2 |= 4;
		
		cb = (CheckBox) findViewById(R.id.cbCost);
		if( cb.isChecked() )
			column3 |= 1;
		
		cb = (CheckBox) findViewById(R.id.cbOrder);
		if( cb.isChecked() )
			column3 |= 2;
	
		CfgNpl config = (CfgNpl) ConfigManager.getConfig();
		config.priceClmn2Type = column2;
		config.priceClmn3Type = column3;
		ConfigManager.save();
	}
}
