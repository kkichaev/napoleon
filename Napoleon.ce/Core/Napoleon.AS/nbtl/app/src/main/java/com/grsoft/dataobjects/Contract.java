package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.database.TableInfo;

@TableInfo(name="contract", keyFields="created")
public class Contract extends CreateDocDataObject {
	public String def = "";
	
	public List<ContractItem> items = new ArrayList<ContractItem>();

}
