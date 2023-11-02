package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends OrgPrint {
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();

	@Scale(value=Consts.SUM_SCALE)
	public int balance;
	
	/***
	 * Печатать артикул в торг12
	 */
	public int psa = 0;
	
	public String basis;
	public String ido = "";
	
	public String payInn = "";
}
