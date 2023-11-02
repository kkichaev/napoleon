package com.grsoft.dataobjects;

import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class ExistItem extends DataObject {
	@FieldOrder(order = 0)
	public String id = "";
	
	@FieldOrder(order = 1)
	public int priz = 0;
	
	@FieldOrder(order = 2)
	public int sred = 0;

	@Scale(value = Consts.SUM_SCALE)
	@FieldOrder(order = 3)
	public int cost = 0;

	@FieldOrder(order = 4)
	public int present = 0;

	public ExistItem() {}
	
	public ExistItem(ExistMatrix em) {
		id = em.id;
		priz = em.priz;
		sred = em.sred;
	}
}
