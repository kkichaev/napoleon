package com.grsoft.prch_order;

import java.util.ArrayList;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.prch_order.dataobjects.Gate;
import com.grsoft.util.Util;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

public abstract class EditPage extends Fragment {
	
	protected Gate gate;
	
	public void bind(Gate g) { gate = g; }

	public abstract String getTitle();
	public abstract void write(Gate g);
	
	protected void setEditText(View v, int[] ids, String[] fields) {
		int idx = 0;
		for(int id : ids) {
			EditText ed = (EditText)v.findViewById(id);
			if(ed != null) {
				Object val = gate.getFieldValue(fields[idx]);
				if(val instanceof String)
					ed.setText((String)val);
				else if(val instanceof Integer) {
					int scale = gate.getFieldScale(fields[idx]);
					if(scale == 0)
						ed.setText(Util.IntToScaleStr((Integer)val, 0));
					else
						ed.setText(Util.IntToScaleStr((Integer)val, scale, Util.DEC_DELIM, false));
				}
			}
			idx++;
		}
	}
	
	protected void setSpinners(View v, int[] ids, String[] fields, String[] keys) {
		ConfigImpl ci = new ConfigImpl();
		
		int idx = 0;
		for(int id : ids) {
			Spinner sp = (Spinner)v.findViewById(id);
			if(sp != null) {
				String selVal = "";
				Object val = gate.getFieldValue(fields[idx]);
				if(val instanceof String)
					selVal = (String)val;
				SpinnerHelper.loadSpinner(ci, keys[idx], new ArrayList<CharSequence>(), sp, selVal);
			}
			idx++;
		}
		ci.close();
		
	}
}
