package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends OrgPrint {
	@Scale(value=Consts.SUM_SCALE)
	public int debt;
//	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
	public List<MatrixName> matrixName = new ArrayList<MatrixName>();
}
