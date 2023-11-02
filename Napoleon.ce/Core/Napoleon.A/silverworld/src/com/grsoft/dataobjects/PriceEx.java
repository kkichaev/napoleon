package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class PriceEx extends Price {
	/***
	 * Описание
	 */
	public String desc = "";
	
	/***
	 * Средний вес изделия
	 */
	@Scale(value=Consts.WEIGHT_SCALE)
	public int avgw = 0;
	
	/***
	 * Цена за гр
	 */
	@Scale(value=Consts.SUM_SCALE)
	public int costgr = 0;
	
	/***
	 * Артикул xxxxaссf - код коллекции, A - тип изделия, cc - тип камня, f - неиспользуется
	 */
	public String art = "";
	
	public static final int RING_FLAG_POS = 4;
	public boolean isRing(){
		final String RING_FLAG = "К";
		return art.length() >= 5 &&
				art.substring(RING_FLAG_POS, RING_FLAG_POS +1).toUpperCase().equals(RING_FLAG);
	}
}
