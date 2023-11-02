package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PriceEx extends Price {
	@Scale(value = Consts.QTY_SCALE)
	public int quant = 0;

	public Date date;

	@Scale(value = Consts.QTY_SCALE)
	public int inqty = 0;
}
