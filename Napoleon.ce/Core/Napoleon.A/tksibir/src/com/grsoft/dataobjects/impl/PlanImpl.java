package com.grsoft.dataobjects.impl;

import android.content.Context;

import com.grsoft.dataobjects.Plan;
import com.grsoft.napoleon.PlanEdit;
import com.grsoft.napoleon.documents.CreatableDocument;

public class PlanImpl extends CreatableDocument<Plan> {

	@Override
	public void open(Context context) {
		PlanEdit.open(context, this);
	}

}
