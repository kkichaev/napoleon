package com.grsoft.dataobjects.impl;

public class DebtPkoImpl extends PkoImpl {
	@Override public long sum() { return -data.sum; }
}
