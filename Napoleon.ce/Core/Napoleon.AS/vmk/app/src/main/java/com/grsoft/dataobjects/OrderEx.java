package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	public int boy = 0;
	public int bonus = 0;

	@Scale(value = Consts.SUM_SCALE)
	public int dover_sum;

	public int check = 0;

	public String cmtWH = "";
	public String cmtDlv = "";
}
