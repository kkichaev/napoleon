package com.grsoft.dataobjects.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ArchSales;
import com.grsoft.dataobjects.BCItem;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.PriceSalesQty;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
import com.grsoft.dataobjects.SalesItem;
import com.grsoft.dataobjects.SalesItemEx;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.SalesDetailFromOrder;
import com.grsoft.napoleon.SalesPriceCount;
import com.grsoft.napoleon.documents.ArchSalesDoc;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.SalesDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.content.Context;
import android.content.Intent;

public class SalesImplEx extends SalesImpl {

	public static final int ITEM_COMPLETE = -1;
	public static final int ITEM_ADDED = -2;
	public static final int FAIL_NO_ITEM = 1;
	public static final int FAIL_ALREADY_HAVE = 2;
	public static final int FAIL_ITEM_COMPLETE = 3;
	public static final int FAIL_DOC_FINISHED = 5;
	public static final int FAIL_BC_PARSING = 6;

	static Date CHECK_BC_DATE;

	static  {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, 2022);
		c.set(Calendar.MONTH, Calendar.OCTOBER);
		c.set(Calendar.DAY_OF_MONTH, 1);

		CHECK_BC_DATE = Util.getDayStart(c.getTime());
	}

	Integer needScanned = null;

	@Override
	public void editItem(long itemRowid, Context context) {
		SalesPriceCount.open(context, itemRowid, this);
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Sales> copy) {
		super.postCopyProcess(copy);
		
		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx) pi.getData();
		List<OrderItem> items = new ArrayList<OrderItem>();
		for(OrderItem oi : data.items)
			items.add(oi);
		
		data.items.clear();
		for(OrderItem oi : items) {
			pe.id = oi.id;
			pi.read();
			
			List<PriceSalesQty> prt = pe.distrubuteFIFO(oi.qty);
			int qty = 0;
			for(PriceSalesQty psq : prt)
				qty += psq.qty;
			
			if(qty > 0) {
				SalesItemEx se = (SalesItemEx) createNewItem(pe, qty, oi.cost, oi.inPack());
				data.items.add(se);
				se.party = prt;
				
				pe.remove(prt);
				pi.write();
			}
		}
		pi.close();
	}

	String makeBarcode() {
		String s = UUID.randomUUID().toString().replace("-", "");
		BigInteger big = new BigInteger(s, 16);
		return big.toString();
	}

	@Override
	public void initFromOrder(OrderImplBase<?> src, GpsCoord location) {
		SalesImpl si = findSales((OrderImplEx) src);
		if(si != null) {
			read(si.getRowid(), false);
			return;
		}
		SalesEx se = (SalesEx) data;
		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx) oi.getData();
		o.id = se.id;
		oi.read();
		oi.close();

		se.taxType = o.taxType;
		se.useTax = se.taxType != OrgEx.TAX_NONE ? 1 : 0;

		se.barcode = makeBarcode();

		super.initFromOrder(src, location);
	}
	
	public static SalesImpl findSales(OrderImplEx doc) {
		SalesImplEx si = null;
		OrderEx src = (OrderEx) doc.getData();
		if(src.orderNumber.length() > 0) {
			String where = "id = '" + src.id + "' and orderBaseNumber='" + src.orderNumber + "'";
			DbReader r = new DbReader();
			SalesEx se = new SalesEx();
			if(r.select(se, se.getTableName(), where)) {
				si = new SalesImplEx();
				si.getData().created = se.created;
				si.read();
				si.close();
			}
			r.close();
		}
		
		return si;
	}
	
	@Override
	public boolean delete() {
		if( isExported() )
			return true;
		if( data.number.length() > 0 && data.id.length() > 0 && data.items.size() > 0 ) {
			ArchSales as = new ArchSales();
			String table = DataObjectInfo.getInstance().getTableName(Sales.class);
			DbReader r = new DbReader();
			if( r.select(as, table, "created="+data.created.getTime()) ) {
				DbWriter w = new DbWriter();
				as.params = 0;
				w.insertRecord(as);
				w.close();
				ArchSalesDoc.instance().refreshDocSum(data.id);
			}
		}
		
		if( !super.delete() )
			return false;

		SalesDoc.instance().refreshDocSum(data.id);
		return true;
	}
	
	@Override
	protected void processInit(OrderImplBase<?> src) {
		SalesEx se = (SalesEx)data;

		se.orderBaseDate = src.data.date;
		se.orderBaseNumber = ((OrderEx)src.data).orderNumber;

		initSalesOrgData(src.getData().id);
	}

	void initSalesOrgData(String id) {
		SalesEx se = (SalesEx)data;

		OrgImpl oi = new OrgImpl();
		OrgEx o = (OrgEx) oi.getData();
		o.id = id;
		oi.read();
		oi.close();

		data.sumType = o.costype;
		se.taxType = o.taxType;
		se.useTax = se.taxType != OrgEx.TAX_NONE ? 1 : 0;

		if(data.supplyercode.length() == 0) {
			DbReader r = new DbReader();
			FirmEx f = new FirmEx();
			if (r.select(f, f.getTableName(), "", "id")) {
				se.supplyercode = f.id;
			}
			r.close();
		}
	}
	
	@Override
	public void open(Context context) {
		if(((SalesEx)data).orderBaseNumber.length() > 0) {
			SalesDetailFromOrder.open(context, this);
		} else
			super.open(context);
	}

	public boolean isScanned() {
		if(data.created.compareTo(CHECK_BC_DATE) <= 0) {
			return true;
		}

		PriceImpl pi = new PriceImpl();
		PriceEx pe = (PriceEx) pi.getData();

		for (OrderItem sie : data.items) {
			pe.id = sie.id;
			if(!pi.read() || pe.barcode.length() == 0)
				continue;
			if(!((SalesItemEx)sie).isScanned()) {
				pi.close();
				return false;
			}
		}

		pi.close();
		return true;
	}
	
//	@Override
//	public boolean delete() {
//		if( isExported() )
//			return true;
//		return super.delete();
//	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, long cost, boolean inPack) {
		PriceEx pe = (PriceEx) priceImpl.getData();
		SalesItemEx se = (SalesItemEx) findItem(pe.id);
		if(se != null) {
			pe.add(se.party);
			se.party.clear();
		}
		List<PriceSalesQty> prt = pe.distrubuteFIFO(qty);

		return updateItem(priceImpl, prt, (int)cost, inPack);
	}
	
	public boolean updateItem(PriceImpl pi, List<PriceSalesQty> party, int cost, boolean inPack) {
		PriceEx pe = (PriceEx) pi.getData();
		needScanned = null;
		
		SalesItemEx se = (SalesItemEx) findItem(pe.id);
		if(se != null) {
			pe.add(se.party);
		}
		int qty = 0;
		for(PriceSalesQty psq : party)
			qty += psq.qty;
		
		if(qty == 0) {
			if(se != null) {
				data.items.remove(se);
				pi.write();
				write();
			}
			return true;
		} 
		
		if(se == null) {
			se = (SalesItemEx) createNewItem(pe, qty, cost, inPack);
			data.items.add(se);
		} else {
			se.qty = qty;
			se.countTax(data, pe.tax1);
		}
		se.party = party;

		// если уменьшили кол-во удалим шк
		if(se.qty / Consts.QTY_SCALE < se.barcodes.size()) {
			se.barcodes.clear();
		}

		pe.remove(party);
		pi.write();
		write();
		
		return true;
	}
	
	@Override
	protected void updateQtyPrice(PriceImpl priceImpl, int priceUpdate) {
		Price price = priceImpl.getData();
		if( priceUpdate != 0 && checkPriceQty() ) {
			price.vanQty += priceUpdate;
			priceImpl.write();
		}
	}
	
	public void refreshDocSum() {
		getDocumentType().refreshDocSum(data.id);
		DebtDoc.instance().refreshDocSum(data.id);
	}

	public void inputIncass(final Context context) {
		if( !isEditable() )
			return;

		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public void applayInput(int value, Object... params) {
				
				if (!isEditable())
					return;
				
				((SalesEx)data).incass = value;
				write();
				close();
				
				if (context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
			}

			@Override public long getValue() { return ((SalesEx)data).incass; }
		}, Consts.SUM_SCALE, true, context.getString(R.string.input_value), false);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		((SalesEx)data).barcode = makeBarcode();
		initSalesOrgData(data.id);
	}

	public int scanned() {
		int s = 0;
		for(OrderItem oi : data.items) {
			s += ((SalesItemEx)oi).barcodes.size();
		}
		return s;
	}

	public int addBarcode(String barcode) {
		if(barcode.length() < 16)
			return FAIL_BC_PARSING;
		String bcStrip = barcode.replace("\u001d", "");
		String mark = bcStrip.substring(2, 16);
		String where = "substr('00000000' || barcode, -14) = '" + mark + "'";

		Set<String> items = new HashSet<>();
		for(PriceEx pe : DbReader.fetch(PriceEx.class, where) ){
			items.add(pe.id);
		}

		if(items.size() == 0) {
			return FAIL_NO_ITEM;
		}

		int reason = 0;
		for(OrderItem oi : data.items) {
			if(!items.contains(oi.id))
				continue;

			SalesItemEx sie = (SalesItemEx) oi;
			if(sie.isScanned()) {
				if(reason == 0)
					reason = FAIL_ITEM_COMPLETE;
				continue;
			}
			if(sie.haveBC(barcode)) {
				return FAIL_ALREADY_HAVE;
			}

			BCItem bci = new BCItem();
			bci.mark = barcode;
			sie.barcodes.add(bci);
			reason = sie.isScanned() ? ITEM_COMPLETE : ITEM_ADDED;
			write();
			break;
		}

		return reason == 0 ? FAIL_NO_ITEM : reason;
	}

	public int need_scanned() {
		if(needScanned == null) {
			int q = 0;
			PriceImpl pi = new PriceImpl();
			PriceEx pe = (PriceEx) pi.getData();

			for (OrderItem sie : data.items) {
				pe.id = sie.id;
				if(!pi.read() || pe.barcode.length() == 0)
					continue;
				q += sie.qty / Consts.QTY_SCALE;
			}
			pi.close();
			needScanned = q;
		}

		return needScanned;
	}
}
