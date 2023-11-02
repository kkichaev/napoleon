package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.impl.DbObject;

public class NapoleonAppPre extends NapoleonApp {

	@Override
	protected void initDocTypes() {
		super.initDocTypes();
		PriceCount.activity = PriceCountEx.class;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
}
