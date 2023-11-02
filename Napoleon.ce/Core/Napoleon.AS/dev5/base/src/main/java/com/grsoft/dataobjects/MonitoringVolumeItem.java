package com.grsoft.dataobjects;
import com.grsoft.aceteam.R;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class MonitoringVolumeItem extends DataObject {
	@FieldOrder(order=0)
	@Scale(value=Consts.SUM_SCALE)
	public int volume;
	
	@FieldOrder(order=1)
	@Scale(value=Consts.SUM_SCALE)
	public int cost;

	@FieldOrder(order=2)
	public int qty;
}
