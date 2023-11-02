package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="NewClient", keyFields="created")
@ServerInfo(name="NewClient")
public class NewClient extends CreateDocDataObject implements PhotoListDoc {
	
	public String inn = "";
	public String address = "";
	public String name = "";
	
	public String ogrn = "";
	public String kpp = "";
	public String legalAdr = ""; 
	public String fio = "";
	public String post = "";
	
	public int isBlack = 0;
	public int delay = 0;
	public String phone = "";
	public String email = "";
	public String account = "";
	
	public List<VisitItem> items = new ArrayList<VisitItem>();

	@Override public List<VisitItem> getItems() { return items; }
	@Override public void setItems(List<VisitItem> newItems) { items = newItems; }

	@Override public String getDocName() { return "NewClient"; }
	@Override public String getItemName() { return "NewClientItem"; }

}
