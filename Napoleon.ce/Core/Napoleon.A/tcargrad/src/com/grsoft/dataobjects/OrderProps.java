package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="OrdProps")
public class OrderProps extends DataObject {
	
	public static final int STRING_TYPE = 1;
	public static final int NUMBER_TYPE = 2;
	public static final int DATE_TYPE = 3;
	public static final int TIME_TYPE = 4;
	public static final int BOOL_TYPE = 5;
	
	/**
	 * Соответствует последнему свойству
	 */
	public static final int NUM_EDITORS = 5;
		
	@FieldOrder(order=0)
	public String id;

	@FieldOrder(order=1)
	public String name;

	@FieldOrder(order=2)
	public int type;
}
