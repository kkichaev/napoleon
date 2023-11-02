package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
	public List<MatrixItem> focusedItems = new ArrayList<MatrixItem>();
	
	public int useAvgWeight;
	
	@Scale(Consts.WEIGHT_SCALE)
	public int minWeight;
	
	@Scale(value=Consts.SUM_SCALE)
	public int balance;
	
	public String info = "";
}
