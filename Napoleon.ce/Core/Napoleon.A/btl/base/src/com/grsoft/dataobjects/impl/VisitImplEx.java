package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.VisitItemEx;

public class VisitImplEx extends VisitImpl{
	@Override
	public void addPhoto(byte[] photo) {
		VisitItemEx visitItem = new VisitItemEx();
		visitItem.id = photo;
		getData().items.add(visitItem);
		write();
		close();
	}
}
