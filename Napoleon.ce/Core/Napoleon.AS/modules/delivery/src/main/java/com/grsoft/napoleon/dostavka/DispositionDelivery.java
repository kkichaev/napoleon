package com.grsoft.napoleon.dostavka;

import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.RoutePointImpl;
import com.grsoft.napoleon.DispositionActivity;

public class DispositionDelivery extends DispositionActivity {
	public DbObject<?> initOrg(){
		return new RoutePointImpl();
	}
}
