package com.grsoft.dataobjects;

import java.util.Date;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="NewClient", keyFields="created")
@ServerInfo(name="NewClient")
public class NewClient extends CreateDocDataObject implements Cloneable {
	
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
	public int isNAL = 0;
	public String amount = "";
	public String depth = "";
	public String costype = "";
	public String bik = "";
	public String firma = "";

	public String status = "";

	public int podStatus = 0;

	public int isFact = 0;

	public String statusToText() {
		if(status.equals("ACTIVE")) return "действующий";
		if(status.equals("LIQUIDATING")) return "ликвидируется";
		if(status.equals("LIQUIDATED")) return "ликвидирован";
		if(status.equals("BANKRUPT")) return "банкрот";
		if(status.equals("REORGANIZING")) return "реорганизация";
		return "";
	}
}
