package com.grsoft.dataobjects.impl;
import com.grsoft.aceteam.R;

import android.content.Context;

import com.grsoft.dataobjects.Monitoring;
import com.grsoft.napoleon.MonitoringEdit;
import com.grsoft.napoleon.documents.CreatableDocument;


public class MonitoringDocImpl extends CreatableDocument<Monitoring> {

	@Override
	public void open(Context context) {
		MonitoringEdit.open(context, this);
	}
}
