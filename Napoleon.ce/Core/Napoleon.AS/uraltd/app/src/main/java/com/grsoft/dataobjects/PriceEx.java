package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class PriceEx extends Price {
	public int newitem = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int akc1 = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int akc2 = 0;
}
