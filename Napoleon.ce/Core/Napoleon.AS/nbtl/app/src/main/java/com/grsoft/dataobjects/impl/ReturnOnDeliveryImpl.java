package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.ReturnOnDelivery;
import com.grsoft.napoleon.documents.CreatableDocument;

public class ReturnOnDeliveryImpl extends ReturnImplBaseEx<ReturnOnDelivery>{

	@Override 
	public CreatableDocument<ReturnOnDelivery> createInstance() { 
		return new ReturnOnDeliveryImpl(); 
	}

}
