package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.impl.DbObject;

public class FirmImpl extends DbObject<Firm> 
implements RowData{

	@Override
	public String getCaption() {
		return data.name;
	}

	@Override
	public boolean checkid(String id) {
		return data.id.equals(id);
	}

	@Override
	public String getCode() {
		return data.id;
	}
}
