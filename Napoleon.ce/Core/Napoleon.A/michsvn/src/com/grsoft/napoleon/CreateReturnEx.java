package com.grsoft.napoleon;

import java.util.Calendar;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Return;


public class CreateReturnEx extends CreateReturn{
	@Override
	protected void init(Return r, Org data) {
		super.init(r, data);
		datenextday(r);
	}
	
	private void datenextday(Return o) {
		Calendar c = Calendar.getInstance();
		c.setTime(o.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		o.date = c.getTime();	
	}
}
