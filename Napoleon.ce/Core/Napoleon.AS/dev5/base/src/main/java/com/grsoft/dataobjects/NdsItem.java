package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class NdsItem extends DataObject {
	@FieldOrder(order=0)
	public int nds;
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public int sumtax;
}
