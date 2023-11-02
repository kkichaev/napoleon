package com.grsoft.napoleon;

import java.util.Calendar;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Return;

public class CreateReturnEx extends CreateReturn {
	@Override
	protected void init(Return r, Org data) {
		Calendar c = Calendar.getInstance();
		c.setTime(r.date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		r.date = c.getTime();
		r.sumType = data.costype;
	}
}
