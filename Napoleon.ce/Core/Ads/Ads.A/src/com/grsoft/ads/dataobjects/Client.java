package com.grsoft.ads.dataobjects;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.TableInfo;
import com.grsoft.dataobjects.Contact;
import com.grsoft.dataobjects.DataObject;

@TableInfo(name="clients", keyFields = "id")
public class Client extends DataObject {
	public String id;
	public String name = "";
	public String address = "";
	
	public List<Contact> contacts = new ArrayList<Contact>();
}
