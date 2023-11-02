package com.grsoft.dataobjects;

import com.grsoft.database.TableInfo;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

@TableInfo(name="FolderCoef", keyFields="id")
public class FolderCoef extends DataObject {
	public int id;
	
	@Scale(value=Consts.SUM_SCALE)
	public int coef;	
}
