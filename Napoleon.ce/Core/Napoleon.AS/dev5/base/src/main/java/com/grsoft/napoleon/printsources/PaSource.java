package com.grsoft.napoleon.printsources;
import com.grsoft.aceteam.R;

import java.util.Date;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PaImpl;
import com.grsoft.napoleon.modules.print.util.Dig2Str;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PaSource extends DataSourceAdapter {
	public Date date;
	public Date period; 
	public String orgname = "";
	public String sum = "";
	public String sumText = "";
	public String pkoNumber = "";
	public String sname = "";
	public String sinn = "";
	public String saddress = "";
	public String sbank = "";
	public String fullAgentName = "";
	public String pser = "";
	public String pnumber = "";
	public String pregion = "";
	public String pdata = "";
		
	public PaSource(PaImpl paImpl){
		date = paImpl.getData().date;
		period = paImpl.getData().period;
		
		OrgImpl orgImpl = new OrgImpl();
		orgImpl.getData().id = paImpl.getData().id;
		
		if (orgImpl.read())
			orgname = orgImpl.getData().name;
		
		orgImpl.close();
		
		sum = Util.IntToScaleStr(paImpl.getData().sum, Consts.SUM_SCALE);
		sumText = String.format("%s руб. %02d коп.", Dig2Str.digToText(paImpl.getData().sum /  Consts.SUM_SCALE), 
				paImpl.getData().sum % Consts.SUM_SCALE);
		pkoNumber = paImpl.getData().docnumber;
		
		FirmImpl firmImpl = new FirmImpl();
		firmImpl.getData().id = paImpl.getData().supplyercode;
		
		if(firmImpl.read()){
			sname = firmImpl.getData().name;
			sinn = firmImpl.getData().inn;
			saddress = firmImpl.getData().address;
			sbank = firmImpl.getData().bank;
		}
			
		firmImpl.close();
		
		AgentPrefix ap = AgentPrefix.get();
		
		if(ap != null){
			pser = ap.ser;
			pnumber = ap.number;
			pregion = ap.region;
			pdata = Util.simpleDateFormat.format(ap.data);
		}
	}

	
}
