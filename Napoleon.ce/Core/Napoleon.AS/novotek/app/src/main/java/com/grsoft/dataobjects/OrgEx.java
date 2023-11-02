package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org{
	public String firmCode;
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance;
	
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
}
