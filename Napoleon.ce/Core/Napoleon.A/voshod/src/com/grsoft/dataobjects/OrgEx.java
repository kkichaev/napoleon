package com.grsoft.dataobjects;

import java.util.List;

import com.grsoft.dataobjects.Org;

public class OrgEx extends Org {
	public List<OrgDogovor> dogovors;
	public List<Close> closed;
	
	/***
	 * ID целей отгрузки
	 */
	public String ido = "";
}
