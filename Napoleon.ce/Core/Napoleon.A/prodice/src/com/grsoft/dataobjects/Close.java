package com.grsoft.dataobjects;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;

public class Close extends DataObject{
	
	/***
	 * Фирма закрыта для контрагента
	 */
	@FieldOrder(order=0)
	public String firm = "";
}
