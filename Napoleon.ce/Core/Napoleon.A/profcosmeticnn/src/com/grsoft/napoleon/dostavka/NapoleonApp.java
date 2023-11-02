package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DispatchImpl;
import com.grsoft.dataobjects.impl.DispatchImplEx;

public class NapoleonApp extends NapoleonAppBase {
	@Override
	protected void defineNewType() {
		DispatchImpl.DISPATCH = DispatchImplEx.class;
	}
}
