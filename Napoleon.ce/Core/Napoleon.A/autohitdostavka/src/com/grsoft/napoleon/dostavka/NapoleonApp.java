package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DispatchImpl;
import com.grsoft.dataobjects.impl.DispatchImplEx;
import com.grsoft.napoleon.Features;

public class NapoleonApp extends NapoleonAppBase {
	@Override
	protected void defineNewType() {
		DispatchImpl.DISPATCH = DispatchImplEx.class;
	}
	
	@Override
	protected void initChildActivities() {
		DShipmentEdit.activity = DShipmentEditEx.class;
		DTaskEdit.activity = DTaskEditEx.class;
	}
	
	@Override
	protected void initChildFeatures() {
		super.initChildFeatures();
		Features.ORG_DISPOSITION = true;
	}
}
