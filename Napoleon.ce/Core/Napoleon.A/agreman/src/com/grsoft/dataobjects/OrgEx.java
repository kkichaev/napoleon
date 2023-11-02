package com.grsoft.dataobjects;

import java.util.List;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends Org {
	public List<OrgDogovor> dogovors;
	public String region = "";
	public String info = "";
	public int delay = 0;
	
	@Scale(value=Consts.SUM_SCALE)
	public int limit = 0;
}
