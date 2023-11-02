package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;

public class SalesPrintEx extends SalesPrint {

	public String dogovor;
	public int division;
	public String schfNumber = "";
	
	@PrintInfo(name="СтруктурноеПодразделение")
	public  String supl_division = "Структурное подразделение";
	
	public SalesPrintEx(Sales sales) {
		super(sales);

		SalesEx se = (SalesEx)sales;
		division = se.division;
		schfNumber = se.schfNumber;		
		supl_division = se.divisionName;
	}

	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		dogovor = ((OrgEx)op).dogovor;
	}
	
	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new SupplSourceEx();
		supplSource.setSupplyer(sales.supplyercode);
	}
}

class SupplSourceEx extends SupplSource {
	@PrintInfo(name="ПоставщикИНН")
	public  String supl_f_inn;
	@PrintInfo(name="ПоставщикНаименование")
	public String supl_f_name;
	@PrintInfo(name="ПоставщикБанк")
	public String supl_f_bank;
	@PrintInfo(name="ПоставщикАдрес")
	public String supl_f_address;
	@PrintInfo(name="ПоставщикТелефон")
	public String supl_f_phone;
	@PrintInfo(name="ОнЖЕ")
	public String supl_supl;
	@PrintInfo(name="ДолжностьАгент")
	public  String supl_agent_job;
	
	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		FirmEx fe = (FirmEx)firm;

		supl_f_phone = "";
		supl_f_bank = "";
		supl_f_inn = "";
		supl_f_name = "";
		supl_f_address = "";
		
		AgentPrefixEx ap = (AgentPrefixEx) AgentPrefix.get();
		if( ap != null && ap.jobTitle.length() > 0 )
			supl_agent_job = ap.jobTitle;
		else
			supl_agent_job = "Торговый представитель";
		
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
		
		supl_supl = "он же";
		if( fe.suplAddress.length() > 0 ) {
			supl_f_address = fe.suplAddress;
			if( !fe.suplAddress.equals(fe.address))
				supl_supl = supl_f_name + ", " + supl_f_address; 
		} else
			supl_f_address = fe.address;
		
		if( fe.suplPhone.length() > 0 )
			supl_f_phone = fe.suplPhone;
		else
			supl_f_phone = fe.suplPhone;
	}
}
