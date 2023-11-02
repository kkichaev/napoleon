package com.grsoft.dataobjects;

import java.util.List;

public class IncassEx extends Incass {
	/**
	 * Флаг автоматического расчета суммы
	 */
	public static int AUTO_FLAG = 8;
	
	public String dover = "";
	public String firm = "";
	public List<IncassItem> items;
	
}
