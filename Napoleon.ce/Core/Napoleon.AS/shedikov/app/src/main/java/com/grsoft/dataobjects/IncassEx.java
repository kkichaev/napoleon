package com.grsoft.dataobjects;

import java.util.List;
import com.grsoft.dataobjects.Incass;

public class IncassEx extends Incass {
	/**
	 * Флаг автоматического расчета суммы
	 */
	public static int AUTO_FLAG = 8;
	
	public List<IncassItem> items;

	public String unitCode = "";

	public String number = "";
	
	public String skladid = "";
}
