package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.FolderCoefImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OffTakeHistoryEx extends OffTakeHistory {

	private static final int DEFAULt_COEF_VAL = 120;

	public int getOffTakeCoef(String itemID) {
		FolderCoefImpl folderCoef = new FolderCoefImpl();
		PriceImpl price = new PriceImpl();
		
		price.getData().id = itemID;
		price.read();
		
		folderCoef.getData().id = price.getData().folderID;
		if( folderCoef.read())
			return folderCoef.getData().coef;
		
		int coef = 0;		
		if( coef == 0 ) {
			ConfigImpl config = new ConfigImpl();
			config.getData().key = "OffTakeCoef";
			if( config.read() )
				try{
					String val = config.getData().value;
					final String DOT = ".";
					
					if(val.indexOf(DOT) != -1)
						val = val.replace(".", Util.DEC_DELIM);
					
					coef = Integer.parseInt(val);
				}catch(Exception e){
					e.printStackTrace();
					coef = DEFAULt_COEF_VAL;
				}
			else
				coef = DEFAULt_COEF_VAL;
			config.close();
		}

		price.close();
		folderCoef.close();
		return coef;
	}
	
	class SaleItemEx extends SaleItem {
		public int ret;
	}
	
	class DateSalesEx extends DateSales {

		public DateSalesEx(Date d) {
			super(d);
		}

		public void loadReturn(Return doc) {
			if( doc.items != null )
				for(OrderItem ri : doc.items) {
					SaleItemEx si = (SaleItemEx)items.get(ri.id);
					si.ret += ri.qty;
				}
		}
	}
	
	public class ItemEx extends Item {
		
		public int ret = 0;

		public ItemEx(Date d) {
			super(d);
		}
		
		@Override
		public void copyFrom(SaleItem si) {
			super.copyFrom(si);
			
			ret = ((SaleItemEx)si).ret;
		}
		
		@Override
		public String makeText(boolean firstItem) {
			String text = "";
			
			text += Util.IntToScaleStr((rest + 5) /10 , Consts.SUM_SCALE, Util.DEC_DELIM, false);
			text += "<br>";
			text += Util.IntToScaleStr((offTake + 5) / 10, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			text += "<br>";
			
			if( firstItem ) text+= "<b>";
			text += Util.IntToScaleStr((qty + 5) / 10, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			if( firstItem ) text+= "</b>";

			text += "<br>";
			text += Util.IntToScaleStr((ret + 5) / 10, Consts.SUM_SCALE, Util.DEC_DELIM, false);

			return text;
		}
	}
	
	@SuppressWarnings("serial")
	class SalesEx extends Sales {
		
		@Override
		DateSales findSaleFromDocDate(Date docDate) {
			long days = docDate.getTime() / ((long)3600 * 24 * 1000);  			
			int i = 0;
			DateSales ret = null;
			for( ; i < size(); i++ ) {
				long cd = get(i).date.getTime() / ((long)3600 * 24 * 1000);
				if( cd >= days ) {
					if( i != 0 )
						ret = get(i-1);
					break;
				}
			}
			
			return ret;
		}		
	}

	
	@Override protected SaleItem createSaleItem() { return new SaleItemEx(); }
	@Override protected DateSales getDateSales(Date d) { return new DateSalesEx(d); }	
	@Override protected Sales getSales() { return new SalesEx(); }
	@Override protected Item getSaleItem(Date d) { return new ItemEx(d); }
	
	class SortSales implements Comparator<DateSales> {
		@Override public int compare(DateSales object1, DateSales object2) {
			return object2.date.compareTo(object1.date);
		}		
	}
	
	public OffTakeHistoryEx(String orgId, boolean fromOrders) {
		sales = getSales();

		loadRest(orgId);
		
		if( fromOrders )
			loadOrders(orgId);
		else
			loadDeliveries(orgId);
		
		loadRets(orgId);
		Collections.sort(sales, new SortSales());
	}

	private void loadRets(String orgId) {
		DocList docs = new DocList(ReturnImpl.class, "id='" + orgId + "'", "date");
		for(Document<?> rd : docs) {
			Return doc = (Return)rd.getData();
			DateSalesEx ds = (DateSalesEx)sales.findSaleFromDocDate(doc.date);
			if( ds != null )
				ds.loadReturn(doc);		
		}
		docs.close();
	}
	
	@Override
	public ArrayList<Item> getHistory(String itemId) {
		ArrayList<Item> res = new ArrayList<Item>();
		
		if( sales.size() > 0 ) {
			Calendar c = Calendar.getInstance();
			
			int dayOfWeek = 0, salesCount = 0;
			int prevR = 0, prevSales = 0;
					
			DateSales firstItem = sales.get(0);
			c.setTime(firstItem.date);
			dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			
			for(int i=1; i<sales.size(); i++) {
				DateSales item = sales.get(i);
				ItemEx ri = (ItemEx) getSaleItem(item.date);
	
				SaleItem s = item.items.get(itemId);			
				
				if( s != null ) {
					ri.copyFrom(s);
					ri.offTake = ri.rest + ri.qty - ri.ret - prevR;
					prevR = ri.rest;
					
					c.setTime(item.date);
					int dw = c.get(Calendar.DAY_OF_WEEK);
					if( dw == dayOfWeek && salesCount++ < 2 ) {
						if(prevSales < ri.offTake)
							prevSales = ri.offTake;
					}
				}
				res.add(ri);
			}
			
			ItemEx ri = (ItemEx) getSaleItem(firstItem.date);
			ri.offTake = prevSales;
			ri.qty = calcQty(ri, getOffTakeCoef(itemId));
			res.add(0, ri);
		}
		return res;
	}
	
	public int calcQty(ItemEx ri, int coef) {
		int qty = (int)((long)ri.offTake * coef / Consts.SUM_SCALE) - ri.rest + ri.ret;
		return (qty <= 0 ) ? 0 : qty;
	}
}
