package com.grsoft.dataobjects;

import android.graphics.Color;

public class CellTypes {
	public static int CELL_TYPE_UNDEF = 0;
	public static int CELL_TYPE_COLD = 1;
	public static int CELL_TYPE_HOT = 2;

	public static int getBackColor(int type) {
		return type == CELL_TYPE_UNDEF ? Color.TRANSPARENT :
			type == CELL_TYPE_COLD ? Color.BLUE :
			type == CELL_TYPE_HOT ? Color.RED :
			Color.TRANSPARENT;		
	}
}
