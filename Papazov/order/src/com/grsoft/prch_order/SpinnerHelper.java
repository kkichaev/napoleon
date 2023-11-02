package com.grsoft.prch_order;

import java.util.List;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.impl.ConfigImpl;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SpinnerHelper {
	public static final char SEP_SYMBOL = ';';
	
	public static void loadSpinner(ConfigImpl config, String key, List<CharSequence> values, Spinner s, String selected) {
		Config c = config.getData();
		c.key = key;
		int sel = -1;
		if( config.read())
			sel = makeList(c.value, values, selected);
		
		
		ArrayAdapter<CharSequence> aa = new ArrayAdapter<CharSequence>(s.getContext(), android.R.layout.simple_spinner_item, values);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(aa);
		if(sel < 0)
			sel = 0;
		if( sel < s.getCount())
			s.setSelection(sel);
	}

	public static int makeList(String value, List<CharSequence> values, String selected) {
		int sel = -1;
		int pos = value.indexOf(SEP_SYMBOL); 
		
		while(pos != -1) {
			String f = value.substring(0,pos);
			
			if( selected != null && f.equals(selected))
				sel = values.size();
			
			value = value.substring(pos+1);
			values.add(f);
			pos = value.indexOf(SEP_SYMBOL); 
		}

		if(pos == -1 && value.length() > 0) {
			if( selected != null && value.equals(selected))
				sel = values.size();
			values.add(value);
		}
		return sel;
	}
}
