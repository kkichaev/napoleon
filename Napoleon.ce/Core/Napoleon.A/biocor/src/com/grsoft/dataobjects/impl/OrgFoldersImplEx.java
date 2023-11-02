package com.grsoft.dataobjects.impl;

import java.util.Calendar;
import com.grsoft.dataobjects.OrgFoldersEx;


public class OrgFoldersImplEx extends OrgFoldersImpl {
	
	@Override
	public long write() {
		((OrgFoldersEx)data).modify = Calendar.getInstance().getTime();
		((OrgFoldersEx)data).params = 0;
		return super.write();
	}
	
	@Override
	public boolean delete() {
		getData().items.clear();
		write();
		
		return true;
	}
}
