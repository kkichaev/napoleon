package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;

public class Party extends DataObject implements Cloneable{
	/***
	 * Дата прихода партия(когда он был закуплен)
	 */
	@FieldOrder(order=0)
	public Date date;
	
	/***
	 * Имя группы
	 */
	@FieldOrder(order=1)
	public String ntd;
	
	/***
	 * Количество
	 */
	@Scale(value=1000)
	@FieldOrder(order=2)
	public int qty;
	
	@FieldOrder(order=3)
	public String owner; 
	
	@Override
	public DataObject clone(){
		return super.clone();
	}
}
