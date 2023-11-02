package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;

public class SupplSourceEx extends SupplSource {
	@PrintInfo(name="ÊÏÏ")
	public  String supl_kpp = "";
	@PrintInfo(name="ÄÎÂÅĞÅÍÍÎÑÒÜ")
	public  String supl_dov = "";
	@PrintInfo(name="ÀÂÒÎ")
	public  String supl_avto = "";
	@PrintInfo(name="ÍÎÌÅĞÀÂÒÎ")
	public  String supl_navto = "";
	public String driver = "";
	@PrintInfo(name="ÏîëíîåÍàèìåíîâàíèå")
	public String supl_full_name = "";
	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		supl_kpp = ((FirmEx)firm).kpp;
		
		supl_full_name = firm.fullName;
		supl_name = firm.name;
		
		AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();
		if( ap != null ){
			supl_dov = ap.dov;
			supl_avto = ap.avto;
			supl_navto = ap.navto;
			driver = ap.driver;
		}
	}
}
