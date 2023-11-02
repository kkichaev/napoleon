package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="MatrixOrder")
@ServerInfo(name="MatrixOrder")
public class MatrixOrder extends DataObject {
	public String userid;
	
	public List<MatrixOrderItem> items = new ArrayList<MatrixOrderItem>();
}
