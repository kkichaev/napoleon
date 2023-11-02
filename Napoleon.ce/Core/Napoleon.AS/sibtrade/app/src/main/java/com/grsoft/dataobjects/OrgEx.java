package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	@Scale(value=Consts.SUM_SCALE)
	public int debt;
	
	@Scale(value=Consts.SUM_SCALE)
	public int minOrder;

	public int firm;

	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
}
