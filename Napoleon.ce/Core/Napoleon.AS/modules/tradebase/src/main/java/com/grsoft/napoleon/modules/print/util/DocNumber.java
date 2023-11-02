package com.grsoft.napoleon.modules.print.util;

import com.grsoft.dataobjects.DataObject;

public class DocNumber extends DataObject {
	public String number;
	
	public String getPrefix() { return prefix; }
	public void setPrefix(String newPrefix) { prefix = newPrefix; }
	
	String prefix;
}

