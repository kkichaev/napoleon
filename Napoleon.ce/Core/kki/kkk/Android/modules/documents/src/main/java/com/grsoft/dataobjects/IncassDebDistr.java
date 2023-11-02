package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class IncassDebDistr extends Incass {
	/**
	 * Флаг автоматического расчета суммы
	 */
	public static int AUTO_FLAG = 8;
	
	public List<IncassDebDistrItem> items = new ArrayList<IncassDebDistrItem>();
	
	public int autoMode = 0;
}
