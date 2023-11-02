package com.grsoft.napoleon.printsources;

import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.AgentPrefixEx;
import com.grsoft.dataobjects.Firm;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgPrint;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.OrgDogovorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class SalesPrintEx extends SalesPrint{

	public String basis;
	public String packQtyStr;
	boolean useArticle;
	
	public SalesPrintEx(Sales sales) {
		super(sales);
	
		packQtyStr = Util.IntToScaleStr(packQty, Consts.QTY_SCALE);
		if( packQty % Consts.QTY_SCALE != 0 ) {
			while( packQtyStr.endsWith("0") )
				packQtyStr = packQtyStr.substring(0, packQtyStr.length() - 1);
		}
		
		String dogId = ((SalesEx)sales).dogId;
		if( dogId != null && dogId.length() > 0) {
			OrgDogovorImpl di = new OrgDogovorImpl();
			OrgDogovor od = di.getData();
			od.id = dogId;
			if( di.read() )
				basis = od.basis;
			di.close();
		}
	}
	
	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
		return new SalesItemPrintEx((SalesItem)item, index, sales.sumType, useArticle);
	}
	
	@Override
	protected void initOrg(OrgPrint op) {
		super.initOrg(op);
		OrgEx oe = (OrgEx)op; 
		basis = oe.basis;
		useArticle = oe.psa != 0;
		
		if( oe.ido.length() > 0 && oe.ido.equals(oe.id) == false ) {
			OrgImpl oi = new OrgImpl();
			OrgEx payOrg = (OrgEx)oi.getData();
			payOrg.id = oe.ido;
			if( oi.read() ) {
				payAddress = (payOrg.legalAddress != null && payOrg.legalAddress.length() > 0) ? payOrg.legalAddress : payOrg.address;
				payName = (payOrg.fullName != null && payOrg.fullName.length() > 0) ? payOrg.fullName : payOrg.name;
				payPhone = payOrg.phone;
				payBank = payOrg.bank;
				payInn = payOrg.inn;
			}
			oi.close();
		}
		if(oe.payInn.length() > 0)
			payInn = oe.payInn;
	}
	
	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new SupplSourceEx();
		supplSource.setSupplyer(sales.supplyercode);
	}
	
	@Override
	protected SalesPrintItems createPrintItems() {
		return new SalesItems(this);
	}
}

class SalesItems extends SalesPrintItems {

	private static final long serialVersionUID = 1L;

	public String pagePackStr;
	
	public SalesItems(SalesPrint owner) {
		super(owner);
	}
	
	@Override
	public void calculate() {
		super.calculate();
		
		pagePackStr = Util.IntToScaleStr(pagepack, Consts.QTY_SCALE);
		if( pagepack % Consts.QTY_SCALE != 0 ) {
			while( pagePackStr.endsWith("0") )
				pagePackStr = pagePackStr.substring(0, pagePackStr.length() - 1);
		}
	}
}

class SupplSourceEx extends SupplSource {
	@PrintInfo(name="ФактАдрес")
	public String supl_factAddress;
	
	@PrintInfo(name="ДирДолжность")
	public String supl_chiefPost;

	@PrintInfo(name="БухДолжность")
	public String supl_buhPost;
	
	@PrintInfo(name="ПриказАгента")
	public String supl_order;

	@PrintInfo(name="ОнЖе")
	public String supl_fact;

	@Override
	protected void initFirm(Firm firm) {
		super.initFirm(firm);
		
		FirmEx fe = (FirmEx)firm;
		supl_factAddress = fe.factAddress;
		if(supl_factAddress.length() == 0 || fe.address.equals(fe.factAddress)) {
			supl_factAddress = supl_address;
			supl_fact = "Он же";
		} else {
			// есть файтический адрес заполняем полностью грузоотправителя
			supl_fact = supl_name + " " + supl_factAddress;
		}
		
		supl_chiefPost = fe.chiefPost;
		supl_buhPost = fe.buhPost;
		
		AgentPrefixEx ae = (AgentPrefixEx)AgentPrefix.get();
		if( ae != null )
			supl_order = ae.order;
	}
}
