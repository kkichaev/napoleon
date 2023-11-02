package com.grsoft.dataobjects.impl;

public class DebetIncassImpl extends IncassImplEx {
	@Override
	public long sum() {
		return -data.sum;
	}
}
