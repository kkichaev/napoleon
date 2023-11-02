	package com.grsoft.napoleon.printsources;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.PriceSalesQty;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.napoleon.R;
import com.grsoft.types.Scale;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.content.Context;

public class SalesPrintEx extends SalesPrint {

	static Date OLD_CHIF_DATE = null;

	public int curForm = 0;
	
	public String dogovor;
	public String dogDate;
	public String dogNum;
	public String docBase;
	public String barcodenum;
	public String barcode;
	public String barcodetype = "CODE128";
	
	public List<SalesItemPrintEx> partyItems = new ArrayList<SalesItemPrintEx>();
	
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int pageItemCount;
	
	@Scale(value=Consts.QTY_SCALE, hideRest=true)
	public int itemCount;

	public String pageNumber = "";
	
	public SalesPrintEx(Sales sales) {
		super(sales);
		
		//qtyText = "";
		SalesEx se = (SalesEx)sales;
		if(se.orderBaseNumber.length() > 0) {
			SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
			docBase = "Заказ № " + se.orderBaseNumber + " от " + sd.format(se.orderBaseDate);
			dogovor = docBase;
		}
		
		if(se.isExchange != 0) {
			name += " (товарный вид)";
		} else if( se.isExpired != 0 ) {
			name += " (обмен просрочка)";
		}
		
		int idx = 1;
		for(OrderItem oi : se.items) {
			SalesItemEx sie = (SalesItemEx)oi;
			for(PriceSalesQty psq : sie.party) {
				int qty = oi.qty;
				oi.qty = psq.qty;
				
				SalesItemPrintEx sip = new SalesItemPrintEx(sie, idx, 0);
				sip.date = Util.simpleDateFormat.format(psq.date);
				partyItems.add(sip);
				
				oi.qty = qty;
				idx++;
			}
		}
		
		barcodenum = se.barcode; 
	}
	
	/**
	 * Печать или в упаковках или в штуках
	 * @param inPack
	 */
	public void setInPack( boolean inPack) {
		for(SalesItemPrint i : items.getItems()) {
			((SalesItemPrintEx)i).setInPack(inPack);
		}
		
		for(SalesItemPrintEx se : partyItems)
			se.setInPack(inPack);
	}

	@Override
	protected void initSupplyer(Sales sales) {
		super.initSupplyer(sales);
		if(OLD_CHIF_DATE == null) {
			Calendar c = Calendar.getInstance();
			c.set(2020, 12 - 1, 3);
			OLD_CHIF_DATE = c.getTime();
		}
		if(sales.date.compareTo(OLD_CHIF_DATE) < 0) {
			supplSource.buh = "Торосян Армен Степанович";
			supplSource.chief = "Торосян Армен Степанович";
		}
	}

	public void initForm(int res) {
		curForm = res;
		if(curForm == R.raw.upd)
			((SalesEx)sales).totalPageUPD = 1;
		else if(curForm == R.raw.torg12)
			((SalesEx)sales).totalPageNakl = 1;
		else if(curForm == R.raw.schf)
			((SalesEx)sales).totalPageSF = 1;

		if((sales.params & (ParamState.ofExported | ParamState.ofProceeded)) == 0) {
			DbWriter w = new DbWriter();
			w.insertRecord(sales);
			w.close();
		}
	}
	
	@Override
	public void startPage() {
		super.startPage();
		pageItemCount = 0;

		pageNumber = String.format("Страница: %s", totalPage);
		
		if(curForm == R.raw.upd)
			((SalesEx)sales).totalPageUPD++;
		else if(curForm == R.raw.torg12)
			((SalesEx)sales).totalPageNakl++;
		else if(curForm == R.raw.schf)
			((SalesEx)sales).totalPageSF++;
		
		if((sales.params & (ParamState.ofExported | ParamState.ofProceeded)) == 0) {
			DbWriter w = new DbWriter();
			w.insertRecord(sales);
			w.close();
		}
	}
	
	@Override
	public void calculate(SalesItemPrint sip) {
		pageItemCount += ((SalesItemPrintEx)sip).itemCount;
	}

	@Override
	public void initSource(Context context, int res) {
		super.initSource(context, res);
		pageItemCount = 0;
		
		String raw = context.getResources().getResourceEntryName(res);
		
		String prefix = "";
		
		if (raw.equals("torg12"))
			prefix = "T12";
		else if (raw.equals("upd"))
			prefix = "UPD";
		else if (raw.equals("schf"))
			prefix = "SFV";
		
		barcode = prefix + barcodenum;
	}
	
	@Override
	protected void initOrg(Org op) {
		super.initOrg(op);
		
		OrgEx oe = (OrgEx)op;
		
		dogDate = "";
		dogNum = "";
		dogovor = oe.dogovor;
		if(oe.divName.length() > 0)
			name = oe.divName;
		
		if(oe.dogNum.length() > 0) {
			dogDate = oe.dogDate.getYear() < 100 ? "" : Util.simpleDateFormat.format(oe.dogDate);
			dogNum = oe.dogNum;
			
			dogovor = dogNum + " от " + dogDate;
		}
		
		docBase = "Договор " + dogovor;
		
		if(oe.payBank.length() > 0)
			payBank = oe.payBank;
		if(oe.payInn.length() > 0)
			payInn = oe.payInn;
		if(oe.payName.length() > 0)
			payName = oe.payName;
		if(oe.payPhone.length() > 0)
			payPhone = oe.payPhone;
		
		if(oe.kpk > 0){
			name = ((SalesEx)sales).orgName;
			address = ((SalesEx)sales).orgAddress;
		}
	}
	
	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index, OrderItem item) {
		SalesItemPrintEx sip =  new SalesItemPrintEx((SalesItem)item, index, sales.sumType);
		itemCount += sip.itemCount;
		return sip;
	}
	
	@Override
	protected SupplSource createSupplSource() {
		return new SupplSourceEx();
	}
}
