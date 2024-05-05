package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.Date;

public class DeliveryEx extends Delivery {
	public int dlv_pos = 0;
	public int hidden = 0;

	@Scale(value = Consts.SUM_SCALE)
	public int debet = 0;

	@Override
	public boolean isOverdue() {
		return debet > 0 && (new Date()).compareTo(payDate) > 0;
	}
}
