package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.Sklad;

public class SkladImpl extends DbObject<Sklad> 
implements RowData{
	public boolean isCheckPack() {
		return (data.flags & Sklad.CHECK_PACKS) == Sklad.CHECK_PACKS;
	}

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
