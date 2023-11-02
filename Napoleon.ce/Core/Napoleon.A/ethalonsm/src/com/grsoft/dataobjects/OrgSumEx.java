package com.grsoft.dataobjects;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgSumEx extends OrgSum {
	@Scale(value=Consts.SUM_SCALE)
	public int sum2 = 0;
}
