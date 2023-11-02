package com.grsoft.dataobjects.impl;

import com.grsoft.util.Util;

public class IncassImplEx extends IncassImpl {
	@Override
	public boolean isEditable() {
		return super.isEditable() || Util.isToday(data.date);
	}
}
