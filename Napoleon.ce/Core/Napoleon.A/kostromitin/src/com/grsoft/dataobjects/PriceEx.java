package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import android.graphics.Color;

public class PriceEx extends Price {
	public int priority;
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty2;

	public int isLukoil;
	
	public int noDiscount = 0;
	
	public int getPriorityColor() {
		return priority == 1 ? Color.WHITE :
			priority == 2 ? Color.rgb(255, 108, 47):
			priority == 3 ? Color.GREEN:
			priority == 4 ? Color.BLUE :
			Color.TRANSPARENT;
	}
}
