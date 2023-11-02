package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;


public class RemnantsEx extends Remnants  implements OrgUnitable{
	
	@Scale(value=Consts.QTY_SCALE)
	public int ourgrkqty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int ourvtrqty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int ourcmnqty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int cncgrkqty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int cncvtrqty = 0;
	
	@Scale(value=Consts.QTY_SCALE)
	public int cnccmnqty = 0;
	
	public String unitCode = "";

	@Override
	public String getCode() { return unitCode; }

	@Override
	public void setCode(String val) { unitCode = val; }
	
	public List<ConcurentItem> cncs = new ArrayList<ConcurentItem>();
}
