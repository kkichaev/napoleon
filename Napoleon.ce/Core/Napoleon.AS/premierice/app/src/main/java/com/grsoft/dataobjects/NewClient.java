package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="NewClient", keyFields="created")
@ServerInfo(name="NewClient")
public class NewClient extends CreateDocDataObject {
	
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
	
	public Date visitDoc = new Date(1000);
}
