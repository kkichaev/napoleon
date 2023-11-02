package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.VisitEx;


public class VisitImplEx extends VisitImpl {
	@Override
	public void postInit() {
		super.postInit();
		((VisitEx)data).actgs = 1;
		data.remark = "Сфотографируйте акцию \"Золотая полка\"";
	}
}
