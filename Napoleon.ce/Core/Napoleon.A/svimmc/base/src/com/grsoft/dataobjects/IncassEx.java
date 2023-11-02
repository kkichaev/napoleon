package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.dataobjects.Incass;
import com.grsoft.types.Scale;

public class IncassEx extends Incass {
	/**
	 * Флаг автоматического расчета суммы
	 */
	public static int AUTO_FLAG = 8;
	
	public String iddog = "";
	public List<IncassItem> items;
	
	@Scale(value=1)
	public int supplyer;
	
	public String number = "";
}
