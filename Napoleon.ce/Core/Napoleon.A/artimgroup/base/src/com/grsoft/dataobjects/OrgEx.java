package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.types.Scale;
import com.grsoft.util.Consts;

public class OrgEx extends OrgPrint{
	public List<OrgDogovor> dogovors;
	public String text = "";
	@Scale(value = Consts.SUM_SCALE)
	public int deb;
}
