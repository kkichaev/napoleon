package com.grsoft.view;
import com.grsoft.aceteam.R;

import java.util.ArrayList;

import com.grsoft.aceteam.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class DropDownListAdapter<T> extends ArrayAdapter<T> {
    private int parityDrawable = R.drawable.even_row_selector;
    private int oddDrawabler = R.drawable.list_selector;
    
	public DropDownListAdapter(Context context, ArrayList<T> objects) {
		super(context, R.layout.simple_spinner_layout, objects);
	}
	
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View result = super.getDropDownView(position, convertView, parent);
		result.setBackgroundResource(position % 2 != 0 ? 
				parityDrawable : oddDrawabler);
		return result;
	}
}
