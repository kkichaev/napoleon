package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public List<Fridge> fridges = new ArrayList<Fridge>();
	
	@Scale(value = Consts.SUM_SCALE)
	public int minOrder;
		
	public int sklad = 0;

	public String cluster = "";
	
	public List<MatrixItem> matrix = new ArrayList<MatrixItem>();
}
