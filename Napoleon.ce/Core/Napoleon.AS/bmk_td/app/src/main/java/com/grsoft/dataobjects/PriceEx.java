package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

import java.util.ArrayList;
import java.util.List;

public class PriceEx extends Price {
	public List<UnitItem> units = new ArrayList<UnitItem>();
	public int grav = 0;

	@Scale(value = Consts.QTY_SCALE)
	public int min = 0;
}
