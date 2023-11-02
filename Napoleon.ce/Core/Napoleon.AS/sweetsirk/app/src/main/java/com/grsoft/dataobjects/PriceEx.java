package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	public int mask = 1;

	@Scale(value=Consts.QTY_SCALE)
	public int tqty = 0;
	
	public Date tdate = null;

	@Scale(value=Consts.SUM_SCALE)
	public int disc = 0;

	public int expdate = 0;
}
