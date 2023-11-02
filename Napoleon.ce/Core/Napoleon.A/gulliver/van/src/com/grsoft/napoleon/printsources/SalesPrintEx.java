package com.grsoft.napoleon.printsources;

import java.text.SimpleDateFormat;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderExtended;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgExtended;
import com.grsoft.dataobjects.PricePrintEx;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.impl.FirmImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.modules.print.util.Dig2Str;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class SalesPrintEx extends SalesPrint {
	public String datestr;
	public String wdatestr;
	Boolean useTax = null;
	public String agent;
	public String qtyDigText = "";
	
	private static final SimpleDateFormat sdf =  new SimpleDateFormat("dd MMM yyyy");
	private static final SimpleDateFormat sdf3 =  new SimpleDateFormat("\"dd\" MMM yyyy года");
	
	public SalesPrintEx(Sales sales) {
		super(sales);
		int totalqty = 0;
		int totalBrutto = 0;
		
		for(SalesItemPrint item : items.items){
			totalqty += ((SalesItemPrintEx)item).iqty;
			totalBrutto += item.ibrutto;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(Util.IntToScaleStr(totalqty, Consts.QTY_SCALE))
		  .append(" (")
		  .append(Dig2Str.digToText(totalqty / Consts.QTY_SCALE))
		  .append(")");
		
		qtyText = sb.toString();
		
		sb.setLength(0);
		sb.append(Math.round((float)totalBrutto / (float)Consts.WEIGHT_SCALE))
		  .append(" (")
		  .append(getWeightText(totalBrutto))
		  .append(")");
		
		bruttoText = sb.toString();
		
		AgentPrefix ap = AgentPrefix.get();
		agent = ap.name;
		
		qtyDigText = Util.IntToScaleStr(totalPack, Consts.QTY_SCALE, Util.DEC_DELIM, true);
	}
	
	protected boolean getUseTax() {
		return true;
//		if( useTax == null ) {
//			FirmImpl fi = new FirmImpl();
//			FirmEx f = (FirmEx)fi.getData();
//			f.id = sales.supplyercode;
//			fi.read();
//			fi.close();
//			
//			useTax = (f.tax > 0);
//		}
//		
//		return useTax;
	}

	
	@Override
	protected void initSupplyer(Sales sales) {
		supplSource = new SupplSourceEx();
		supplSource.setSupplyer(sales.supplyercode);
		
		String reason = "";
		OrgImpl org = new OrgImpl();
		org.getData().id = sales.id;
		
		if(sales instanceof OrderExtended){
			String docCode = ((OrderExtended)sales).getDogCode();
			
			if(org.read()){
				OrgExtended oe = (OrgExtended)org.getData();
				
				for(OrgDogovor od : oe.getDogovors()) {
					KeyValue kv = new KeyValue(od.id, od.name);
					
					if(kv.key.equals(docCode)) {
						reason = kv.value.toString();
						break;
					}
				}
			}
			org.close();
		}
		
		supplSource.reason = reason;
	}
	
	@Override
	protected SalesItemPrint createItemPrint(Sales sales, int index,
			OrderItem item) {
		SalesItemPrintEx result = new SalesItemPrintEx((SalesItem)item, index, sales.sumType);
		
		PriceImpl priceImpl = new PriceImpl();
		priceImpl.getData().id = item.id;
		
		priceImpl.read();
		priceImpl.close();
		
		PricePrintEx pp = (PricePrintEx) priceImpl.getData();
		result.name = pp.fullname;
		result.id = pp.article;
		
		if( !getUseTax() ) {
			result.itax = 0;
			result.tax = "Без НДС";
			result.sumtax = ""; 
			result.cost = result.icost;
			result.isumwtax = result.isum;
			result.sumwtax = result.sum;
		} 
		
		return result;
	}

	
	@Override
	protected String getWeightText(int totalWeight) {
		return String.format("%s кг.", Dig2Str.digToText(Math.round((float)totalWeight / (float)Consts.WEIGHT_SCALE)));
	}
	
	@Override
	protected SalesPrintItems createPrintItems() {
		return new SalesPrintItemsEx(this, getUseTax());
	}
	
	@Override
	public void init() {
		super.init();
		datestr = sdf.format(sales.date);
		date = sales.date;
		wdatestr = sdf3.format(sales.date);
	}
}
