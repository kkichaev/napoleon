package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public String ido;
	public int noDrop;
	public String formatTT = "";
	public String idChannel = "";
	public String idRetailer = "";
	
	public List<OrgSalesPlace> salesPlaces = new ArrayList<OrgSalesPlace>(); 
}
