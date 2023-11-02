package com.grsoft.dataobjects.impl;

public class PkoImplEx extends PkoImpl {
	@Override
	public boolean delete() {
		if( isExported() )
			return true;
		return super.delete();
	}
}
