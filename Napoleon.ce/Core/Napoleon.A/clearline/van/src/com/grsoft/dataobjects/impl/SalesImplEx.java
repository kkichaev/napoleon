package com.grsoft.dataobjects.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.ArchSales;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.PricePrint;
import com.grsoft.dataobjects.PricePrintEx;
import com.grsoft.dataobjects.PriceSalesQty;
import com.grsoft.dataobjects.Sales;
import com.grsoft.dataobjects.SalesEx;
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

import android.content.Context;

public class SalesImplEx extends SalesImpl {
	
	@Override
	public void editItem(long itemRowid, Context context) {
		SalesPriceCount.open(context, itemRowid, this);
	}
	
	@Override
	protected void postCopyProcess(CreatableDocument<Sales> copy) {
		super.postCopyProcess(copy);
		
		PriceImpl pi = new PriceImpl();
		PricePrintEx pe = (PricePrintEx) pi.getData();
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
	
	@Override
	public void initFromOrder(OrderImplBase<?> src, GpsCoord location) {
		SalesImpl si = findSales((OrderImplEx) src);
		if(si != null) {
			read(si.getRowid(), false);
			return;
		}

		String s = UUID.randomUUID().toString().replace("-", "");
		BigInteger big = new BigInteger(s, 16);
		((SalesEx)data).barcode = big.toString();
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
		((SalesEx)data).orderBaseDate = src.data.date;
		((SalesEx)data).orderBaseNumber = ((OrderEx)src.data).orderNumber;
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = src.getData().id;
		oi.read();
		oi.close();
		data.sumType = o.costype;
	}
	
	@Override
	public void open(Context context) {
		if(((SalesEx)data).orderBaseNumber.length() > 0) {
			SalesDetailFromOrder.open(context, this);
		} else
			super.open(context);
	}
	
//	@Override
//	public boolean delete() {
//		if( isExported() )
//			return true;
//		return super.delete();
//	}
	
	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {
		PricePrintEx pe = (PricePrintEx) priceImpl.getData();
		SalesItemEx se = (SalesItemEx) findItem(pe.id);
		if(se != null) {
			pe.add(se.party);
			se.party.clear();
		}
		List<PriceSalesQty> prt = pe.distrubuteFIFO(qty);

		return updateItem(priceImpl, prt, cost, inPack);
	}
	
	public boolean updateItem(PriceImpl pi, List<PriceSalesQty> party, int cost, boolean inPack) {
		PricePrintEx pe = (PricePrintEx) pi.getData();
		
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
		
		pe.remove(party);
		pi.write();
		write();
		
		return true;
	}
	
	@Override
	protected void updateQtyPrice(PriceImpl priceImpl, int priceUpdate) {
		PricePrint price = (PricePrint) priceImpl.getData();
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

			@Override public int getValue() { return ((SalesEx)data).incass; }
		}, Consts.SUM_SCALE, true, context.getString(R.string.input_value), false);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		String s = UUID.randomUUID().toString().replace("-", "");
		BigInteger big = new BigInteger(s, 16);
		((SalesEx)data).barcode = big.toString();
	}
}
