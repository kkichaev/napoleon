package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;

public class SupplSourceEx extends SupplSource {
	@PrintInfo(name="КПП")
	public  String supl_kpp = "";
	@PrintInfo(name="ДОВЕРЕННОСТЬ")
	public  String supl_dov = "";
	@PrintInfo(name="АВТО")
	public  String supl_avto = "";
	@PrintInfo(name="НОМЕРАВТО")
	public  String supl_navto = "";
	@PrintInfo(name="Водитель")
	public String driver = "";
	@PrintInfo(name="ПолноеНаименование")
	public String supl_full_name = "";

	public String driver2 = "";
	public String inndriver = "";
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
			driver2 = ap.driver2;
			inndriver = ap.inndriver;
		}
	}
}
