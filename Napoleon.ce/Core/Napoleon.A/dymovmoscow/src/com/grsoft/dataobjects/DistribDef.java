package com.grsoft.dataobjects;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.FieldOrder;

@TableInfo(name="ditrsibdef", keyFields="id")
@ServerInfo(name="DistribDef")
public class DistribDef extends DataObject {
	public static String NUMBER_TYPE = "NUMBER";
	public static String BOOL_TYPE = "BOOL";
	
	@FieldOrder(order=0)
	public String id = "";
	@FieldOrder(order=1)
	public int pos = 0;
	@FieldOrder(order=2)
	public String name= "";
	@FieldOrder(order=3)
	public String type = "";	
	@FieldOrder(order=4)
	public int loadPrev = 0;
	@FieldOrder(order=5)
	public int weight = 0;
	
}
