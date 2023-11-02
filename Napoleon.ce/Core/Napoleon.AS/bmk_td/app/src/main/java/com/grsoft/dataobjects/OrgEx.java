package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public List<OrgDiscountItem> discount = new ArrayList<OrgDiscountItem>();
	public List<OrgCosTypeItem> costypes = new ArrayList<OrgCosTypeItem>();
	public List<OrgAgreement> agreements = new ArrayList<>();

	@Scale(value = Consts.SUM_SCALE)
 	public int disc;

	@Scale(value=Consts.SUM_SCALE)
	public int due;
	@Scale(value=Consts.SUM_SCALE)
	public int postdue;
}
