package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.grsoft.dataobjects.GwinnerAgentTask;
import com.grsoft.napoleon.GwinnerTaskEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.Util;

import android.content.Context;

public class GwinnerAgentTaskImpl extends CreatableDocument<GwinnerAgentTask> {

	@Override public void open(Context context) { GwinnerTaskEdit.open(context, this); }

	@Override
	public void postInit() {
		Calendar c = Calendar.getInstance(Locale.getDefault());
		c.setTime(Util.getDate());
		c.add(Calendar.DAY_OF_MONTH, 7);
		data.date = c.getTime();
		data.done = data.created;
	}
	
	public boolean isComplete() { return data.isComplete > 0; }
	public boolean isOutDate() { return !isComplete() && data.date.compareTo(new Date()) < 0; }
	
	@Override
	public String getDescription(Context context) {
		String superText = super.getDescription(context); 
		String nbsp = "&nbsp;&nbsp;&nbsp;&nbsp;";
		superText += nbsp;
		
		String text = "";
		if( isComplete() ) {
			text = superText + " выполнена";
		} else if( isOutDate() ) {
			text = "<font color='red'>" + superText + " <b>не выполнена</b>" + "</font>";
		} else
			text = superText + " <b>не выполнена</b>";
		return text;
	}
	
	@Override public CreatableDocument<GwinnerAgentTask> copy() { return null; }
}
