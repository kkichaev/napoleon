package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public static final int CHECK_REST = 4;
	
	@Scale(value=Consts.SUM_SCALE)
	public int premium;

	@Scale(value = Consts.SUM_SCALE)
	public int plan;
	
	@Scale(value = Consts.SUM_SCALE)
	public int fact; 
	
	public List<Dogovor> dogovors;
	
//	public List<OrgProp> props;

	@Scale(value = Consts.SUM_SCALE)
	public int minPremium;

	@Scale(value = Consts.SUM_SCALE)
	public int minOrder;
	
	public List<OrgDiscountItem> folderDsc = new ArrayList<OrgDiscountItem>();
}
