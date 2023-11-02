package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.VisitEx;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.dataobjects.VisitItemEx;

public class VisitImplEx extends VisitImpl {
	@Override
	protected void itemAdded(VisitItem item) {
		((VisitItemEx)item).script = ((VisitEx)data).script;
	}
}
