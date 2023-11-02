package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;

@TableInfo(name="OrgDogovors", keyFields="id", indexes="ido")
public class OrgDogovor extends DataObject {
	public String name;
	public String basis;
	public String id;
	public String ido;
	
	public List<OrgDogItem> items = new ArrayList<OrgDogItem>();
	
	@Override public String toString() { return name; }
}
