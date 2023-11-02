package com.grsoft.dataobjects;

public class PriceEx extends Price {
	public String idType = "";
	public String packName = "";
	public String thermalState = "";
	public String idBrand = "";
	
	public String getName() { 
		String ret = name;
		if(thermalState.length() > 0)
			ret += " " + thermalState + "/" + packName;
		return ret; 
	} 
}
