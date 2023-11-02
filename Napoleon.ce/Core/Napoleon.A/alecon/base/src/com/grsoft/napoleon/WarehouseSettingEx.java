package com.grsoft.napoleon;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class WarehouseSettingEx extends WarehouseSetting {
	@Override
	protected void setSpinner(Spinner sp, int value) {
		if( value == WarehouseNew.COLUMN_QTY_WH || value == WarehouseNew.COLUMN_QTY_WH_ORD )
			value = WarehouseNew.COLUMN_QTY_ORD;

		String[] values = getResources().getStringArray(R.array.warehouse_column_values_ex);
		ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, values);
		sp.setAdapter(aa);
		
		if( value > 0 ) value--;
		if( value > 4 ) value--;
		
		if( value > 4 ) value = 4;
		
		sp.setSelection(value);
	}
	
	@Override
	protected int getColumnType(int spinnerValue) {
		if( spinnerValue > 0 ) spinnerValue++;
		if( spinnerValue > 4 ) spinnerValue++;
		return spinnerValue;
	}
}
