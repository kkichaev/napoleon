package com.grsoft.napoleon.printsources;

import java.util.Date;
import java.util.List;

import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.NdsItem;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Pko;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PkoImpl;
import com.grsoft.napoleon.modules.print.util.Dig2Str;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class PkoSource extends DataSourceAdapter {
	public String number;
	public Date date;
	public String sum = "";
	@PrintInfo(name="ИНН")
	public String firminn = "";
	@PrintInfo(name="Наименование")
	public String firmname = "";
	@PrintInfo(name="Банк")
	public String firmbank = "";
	@PrintInfo(name="Адрес")
	public String firmaddress = "";
	@PrintInfo(name="Телефон")
	public String firmphone = "";
	public String inn = "";
	public String address = "";
	public String name = "";
	public String phone = "";
	public String bank = "";
	public String sumText = "";
	public String taxText = "";
	public String buh = "";
	@PrintInfo(name="ОКПО")
	public String okpo = "";
	@PrintInfo(name="ПолноеНаименование")
	public String fullName = "";
	
	public PkoSource(PkoImpl pkoImpl){
		Pko pko = pkoImpl.getData();
		
		setMainData(pko.number, pko.date, pko.sum);
		
		setTax(pko.nds);		
		setFirm(pko.supplyercode);		
		setOrg(pko.id);
	}
	
	protected void setMainData(String number, Date date, long sum) {
		this.number = number;
		this.date = date;
		this.sum = Util.IntToScaleStr(sum, Consts.SUM_SCALE);
		this.sumText = String.format("%s руб. %02d коп.", 
				Dig2Str.digToText(sum /  Consts.SUM_SCALE), 
				sum % Consts.SUM_SCALE);
	}
	
	protected void setTax(List<NdsItem> taxItems) {
		for(NdsItem ndsItem : taxItems){
			taxText += String.format(
					"НДС %d%%: %s,", ndsItem.nds, 
					Util.IntToScaleStr(ndsItem.sumtax, Consts.SUM_SCALE));
		}
		
		if (taxText.length() > 0)
			taxText = taxText.substring(0, taxText.length()-1);
	}

	protected void setOrg(String id) {
		OrgImpl orgImpl = new OrgImpl();
		orgImpl.getData().id = id;
		
		if (orgImpl.read()){
			inn = ((OrgPrint)orgImpl.getData()).inn;
			address = ((OrgPrint)orgImpl.getData()).address;
			name = ((OrgPrint)orgImpl.getData()).name;
			phone = ((OrgPrint)orgImpl.getData()).phone;
			bank = ((OrgPrint)orgImpl.getData()).bank;
		}
		
		orgImpl.close();
	}

	protected void setFirm(String supplyercode) {
		FirmImpl firmImpl = new FirmImpl();
		firmImpl.getData().id = supplyercode;
		
		if (firmImpl.read()){
			Firm firm = firmImpl.getData(); 
			firminn = firm.inn;
			firmname = firm.name;
			firmbank = firm.bank;
			firmaddress = firm.address;
			firmphone = firm.phone;
			buh = firm.buh;
			okpo = firm.okpo;
			fullName = firm.fullName;
		}
		
		firmImpl.close();
	}
	
	protected PkoSource() {}
}
