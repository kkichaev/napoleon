package com.grsoft.dataobjects;

import java.util.Date;

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
	
	public String phone = "";
	public String email = "";
	
	public Date visitDoc = new Date(1000);
	public String bank = "";
	public String corr_acc = "";
	public String account = "";
	public int isNDS = 0;
	public String route = "";
	public int isNAL = 0;
	public String typeTT = "";
	public String amount = "";
	public String depth = "";
	public String costype = "";
	public String bik = "";
}
