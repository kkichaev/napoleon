package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrderEx extends Order {
	@Scale(value=Consts.SUM_SCALE)
	public int nac = 0;

	public int locChecked = 0;
	public int retDoc = 0;
}
