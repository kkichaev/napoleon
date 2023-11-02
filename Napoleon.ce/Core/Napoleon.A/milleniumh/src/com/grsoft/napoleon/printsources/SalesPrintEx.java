package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Sales;

public class SalesPrintEx extends SalesPrint {

	public SalesPrintEx(Sales sales) {
		super(sales);
	}

	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new SupplSourceEx();
		supplSource.setSupplyer(sales.supplyercode);
	}
}

class SupplSourceEx extends SupplSource {
	@PrintInfo(name="ПоставщикИНН")
	public  String supl_f_inn = "";
	@PrintInfo(name="ПоставщикНаименование")
	public String supl_f_name = "";
	@PrintInfo(name="ПоставщикБанк")
	public String supl_f_bank = "";
	@PrintInfo(name="ПоставщикАдрес")
	public String supl_f_address = "";
	@PrintInfo(name="ПоставщикТелефон")
	public String supl_f_phone = "";
	
	@PrintInfo(name="Приказ")
	public String supl_order = "";
	
	
	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		FirmEx fe = (FirmEx)firm;

		if( fe.suplBank.length() > 0 )
			supl_f_bank = fe.suplBank;
		else
			supl_f_bank = fe.bank;
		
		if( fe.suplInn.length() > 0 )
			supl_f_inn = fe.suplInn;
		else
			supl_f_inn= fe.inn;
		
		if( fe.suplName.length() > 0 )
			supl_f_name = fe.suplName;
		else
			supl_f_name = fe.name;
		
		if( fe.suplAddress.length() > 0 )
			supl_f_address = fe.suplAddress;
		else
			supl_f_address = fe.address;
		
		if( fe.suplPhone.length() > 0 )
			supl_f_phone = fe.suplPhone;
		else
			supl_f_phone = fe.suplPhone;
	
	
		AgentPrefixEx a = (AgentPrefixEx) AgentPrefix.get();
		if( a != null && a.order.length() > 0 ) {
			buh = a.name;
			chief = a.name;
			supl_order = a.order;
		}
	}
}
