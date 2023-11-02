package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;

public class RfrgAuditItem extends DataObject {
	/**
	 * номер из КИС 
	 */
	@FieldOrder(order=0)
	public String doc_id = "";
		
	/**
	 * номер из точки
	 */
	@FieldOrder(order=1)
	public String fact_id = "";
	
	@FieldOrder(order=2)
	public String fact_rfid = "";
	
	@FieldOrder(order=3)
	public String model = "";
	
	@FieldOrder(order=4)
	public String descr = "";
}
