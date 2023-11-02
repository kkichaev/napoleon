package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="skladitem", keyFields="id,id_i", indexes="id_i")
@ServerInfo(name="SkladItem")
public class SkladItem extends DataObject {
	public String id = "";
	public String id_i = "";
	
	@Scale(value=Consts.QTY_SCALE)
	public int qty = 0;
	
	
	public List<CostItem> cost = new ArrayList<CostItem>();
}
