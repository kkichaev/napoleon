package com.grsoft.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class SelectPeriodHelper extends SelectHelper implements OnClickListener{
	private List<Period> ranges = new ArrayList<Period>();
	private int idxSelected = -1;
	private SelectPeriodListener selectPeriodListener;
	
	interface SelectPeriodListener{
		void onPeriodSelect(int range);
	}
	
	public void setSelectPeriodListener(SelectPeriodListener listener){
		selectPeriodListener = listener; 
	}
	
	public static class Period{
		public String name = "";
		public int range;
	}
	
	public void init(List<Period> ranges, int sel){
		if(ranges != null){
			this.ranges = ranges;
			this.idxSelected = sel;
		}
	}
	
	public Dialog createDialog(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String data[] = new String[ranges.size()];
		for(int i = 0; i < ranges.size(); i ++)
			data[i] = ranges.get(i).name;
		
		builder.setSingleChoiceItems(data, -1, this);
		return builder.create();
	}
	
	@Override
	protected void applySelect(int which) {
		idxSelected = which;
		Period p = ranges.get(which);
		((TextView)getControl()).setText(p.name);
		firePeriodSelect(p.range);
	}

	private void firePeriodSelect(int range) {
		if (selectPeriodListener != null)
			selectPeriodListener.onPeriodSelect(range);
	}
	
	public void prepareDialog(Dialog dialog){
		ListView lv = ((AlertDialog)dialog).getListView();
		
		if(idxSelected >= 0 && idxSelected < ranges.size())
			lv.setItemChecked(idxSelected, true);
	}
	
	public int getSelected(){ 
		int result = 0;
		
		if(idxSelected >= 0 && idxSelected < ranges.size())
			result = ranges.get(idxSelected).range;
		
		return result;
	}
	
	@Override
	public void setControl(TextView view) {
		super.setControl(view);
		
		view.setText(ranges.get(idxSelected).name);
	}
}
