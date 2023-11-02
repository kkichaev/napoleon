package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgEx extends Org {
	public String group = "";
	public String mid = "";

	public List<MatrixItem> top = new ArrayList<MatrixItem>();
	
	public List<Rfrg> refregerators = new ArrayList<Rfrg>();
	
	public List<OrgPriceItem> matrix = new ArrayList<OrgPriceItem>();
	
	public boolean containsInTop(String id) {
		for(MatrixItem mi : top)
			if( mi.id.equals(id))
				return true;
		return false;
	}
}
