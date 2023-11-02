package com.grsoft.dataobjects;

import com.grsoft.dataobjects.Price;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	@Scale(value=Consts.QTY_SCALE)
	public int minPart; // минимальная отгрузка
}
