package com.grsoft.dataobjects.impl;

import android.content.Context;
import com.grsoft.dataobjects.Planogram;
import com.grsoft.napoleon.PlanogramEdit;
import com.grsoft.napoleon.documents.CreatableDocument;


public class PlanogramImpl extends CreatableDocument<Planogram>{

	@Override
	public void open(Context context) { PlanogramEdit.open(context, this); }

}
