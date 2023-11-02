package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public static final int INCASS_FLAG = 0x100;
	
	public int firm;
	public int delay;
	
	public List<OrgDiscount> discount = new ArrayList<OrgDiscount>();

}
