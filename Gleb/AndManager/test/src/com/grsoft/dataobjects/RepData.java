package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

//f1:s,f2:n(3),f3:n(0),f4[f1:s,f2:dt]
@TableInfo(name="RepTest")
public class RepData extends DataObject {
	public String f1;
	
	@Scale(value=Consts.QTY_SCALE)
	public int f2;
	
	public int f3;
	
	public List<RepItem> f4;
}
