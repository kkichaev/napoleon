package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public int payType;
	public int payMode;
    public int tabak = 0;
	public int alp = 0;

	@Scale(value = Consts.SUM_SCALE)
	public long sumTake = 0;
}
