package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

public class OrgData extends CreateDocDataObject {
	public String forma = "";
	public String inn = "";
	public int cash = 0;
	public String number = "";
	
	public List<PhotoItem> photos = new ArrayList<PhotoItem>();
}
