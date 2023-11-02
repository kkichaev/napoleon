package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class OrgDogovor extends DataObject {
	@FieldOrder(order=0)
	public String id;
	
	@FieldOrder(order=1)
	public String name;
	
	@FieldOrder(order=2)
	public int gen;
	
	/***
	 * Основной договор
	 */
	public boolean isGeneral(){
		return gen == 1;
	}
	
	@FieldOrder(order=3)
	public String stopMsg = "";
}
