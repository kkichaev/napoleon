package com.grsoft.dataobjects;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="MinCost", keyFields = "id,id_i")
public class MinCost extends DataObject {
	@FieldOrder(order= 0)
	public String id = "";
	
	@FieldOrder(order= 1)
	public String id_i = "";

	@FieldOrder(order= 2)
	@Scale(value = Consts.SUM_SCALE)
	public int minCost;
}
