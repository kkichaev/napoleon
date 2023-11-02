package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="ReqNewOrg", keyFields="created")
public class ReqNewOrg extends CreateDocDataObject implements PhotoListDoc {
	public String inn = "";
	public String name = "";
	public String jurAddress = "";
	public String address = "";
	public String ogrn = "";
	
	public int sendedPhotos = 0;
	
	
	public List<VisitItem> items = new ArrayList<VisitItem>();

	@Override public List<VisitItem> getItems() { return items; }
	@Override public String getDocName() { return "ReqNewOrg"; }
	@Override public String getItemName() { return "ReqNewOrgItemDoc"; }
	@Override public void setItems(List<VisitItem> newItems) { items = newItems; }
}
