package com.grsoft.ads.dataobjects.impl;

import com.grsoft.ads.dataobjects.OrderEx;

public class OrderImplEx extends OrderImpl 
implements OrderExtended{

	@Override
	public String getCertificate() {
		return ((OrderEx)getData()).certificate;
	}

	@Override
	public String getProtocol() {
		return ((OrderEx)getData()).protocol;
	}

}
