package com.grsoft.ads.dataobjects.impl;

import com.grsoft.ads.dataobjects.OrderEx;
import com.grsoft.ads.dataobjects.OrderPhotoItem;
import com.grsoft.napoleon.documents.PhotoDocument;

public class OrderImplEx extends OrderImpl 
implements PhotoDocument{

	@Override
	public void addPhoto(byte[] photo) {
		OrderPhotoItem orderPhoto = new OrderPhotoItem();
		orderPhoto.id = photo;
		((OrderEx)getData()).photos.add(orderPhoto);
		write();
		close();
	}
	
	public void setMissed(){
		getData().params |= OrderEx.MISSED;
	}

	public boolean isMissed() {
		return (getData().params & OrderEx.MISSED) == OrderEx.MISSED;
	}
}
