package com.grsoft.dataobjects;

import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Incass;

public class IncassEx extends Incass {
	/**
	 * Флаг автоматического расчета суммы
	 */
	public static int AUTO_FLAG = 8;
	
	/***
	 * Была запись
	 */
	public static int SAVED = 16;
	
	public List<IncassItem> items;
	
	public String dvrnum = "";
	
	public Date dvrdate;
}
