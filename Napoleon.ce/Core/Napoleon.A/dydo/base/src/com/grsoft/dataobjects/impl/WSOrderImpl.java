package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimeZone;

import android.content.Context;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.Agents;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrgFolderItem;
import com.grsoft.dataobjects.OrgFolders;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.dataobjects.VandSell;
import com.grsoft.dataobjects.VandSellItem;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.napoleon.CostStrategy;
import com.grsoft.napoleon.CreateOrder;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.WSOrderDetail;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.util.WeekDay;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

public class WSOrderImpl extends OrderImplBase<WSOrder> {
	@Override
	public void editItem(long itemRowid, final Context context) {
		final PriceImpl priceImpl = new PriceImpl();
		priceImpl.read(itemRowid);
		priceImpl.close();
		final Price p = priceImpl.getData();
		
		InputNumberDlg.open(context, new InputNumber() {
			
			@Override
			public int getValue() {
				OrderItem oi = (OrderItem)findItem(p.id);
				return (oi == null) ? 0 : oi.qty;
			}
			
			@Override
			public void applayInput(int value, Object... params) {
				if( !isEditable() )
					return;
				
				boolean inPack = (Boolean)params[0]; 
				if( inPack ) { 
					int qip = p.qtyInPack;
					if( qip == 0 )
						qip = Consts.QTY_SCALE;
					value = (int)((long)value * qip / Consts.QTY_SCALE);
				}
				if( updateQty(priceImpl, value, 0, inPack) && context instanceof DataSetNotify)
					((DataSetNotify)context).notifyDataSetChanged();
				
			}
		}, Consts.QTY_SCALE, true, context.getString(R.string.input_qty), true);
//		PriceCount.open(context, itemRowid, (DbObject<WSOrder>) this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		CreateOrder.open(ctx, this, isOldOrder);
	}

	@Override
	public CreatableDocument<WSOrder> createInstance() {
		return new WSOrderImpl();
	}
	
	@Override public long sum() { return 0; }

	@Override
	public void open(Context context) {
		WSOrderDetail.open(context, this);
	}
	
	@Override protected boolean checkPriceQty() { return false; }

	String getOrgsInRout(Date date) {
		final List<String> orgs = new ArrayList<String>();
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, 1);
		final WeekDay wd = WeekDay.getDayBySystemId(c.get(Calendar.DAY_OF_WEEK));

		DataTraveler.travel(OrgFolders.class, new DataTraveler.Travel<OrgFolders>() {

			@Override
			public boolean travel(DataTraveler<OrgFolders> item) {
				if( WeekDay.compare(WeekDay.getWeekDay(item.data.name), wd) == 0) {
					for(OrgFolderItem oif : item.data.items)
						orgs.add(oif.name);
					return false;
				}
				return true;
			}
		}, null);
		
		StringBuilder ret = new StringBuilder();
		for(String s : orgs) {
			if( ret.length() > 0 )
				ret.append(",");
			ret.append("'").append(s).append("'");
		}
		return ret.toString();
	}
	
	int docCount = 0;
	
	private HashMap<String, Integer> getAvgSales(Date date) {

		docCount = 0;
		HashMap<String, Integer> ret = new HashMap<String, Integer>();
		String orgs = getOrgsInRout(date);
		
		if( orgs.length() > 0 ) {
			final HashMap<String, Integer> ords = new HashMap<String, Integer>();
			Date checkDate = new Date(date.getTime() - 30 * 24 * 3600000l);
			
			String where = "created > " + Long.toString(checkDate.getTime()) + " and id in (" + orgs + ")";
			DataTraveler.travel(VandSell.class, new DataTraveler.Travel<VandSell>() {

				@Override
				public boolean travel(DataTraveler<VandSell> item) {
					docCount++;
					for(VandSellItem vi : item.data.items) {
						if( vi.chek > 0 ) {
							Integer val = ords.get(vi.id);
							if( val == null )
								val = 0;
							ords.put(vi.id, val + vi.chek);
						}
					}
						
					return true;
				}
			}, where);
			
			for(Entry<String, Integer> val : ords.entrySet()) {
				int iv = val.getValue();
				iv = iv / docCount;
				int rest = iv % Consts.QTY_SCALE;
				if( rest != 0 )
					iv += (Consts.QTY_SCALE - rest);
				ret.put(val.getKey(), iv);
			}
		}
		
		return ret;
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		data.date = Util.getDate();
		data.created = Util.getDateTime();
		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		
		final HashMap<String, Integer> load = getAvgSales(data.date);

		data.timeZone = -tz.getOffset(now.getTime()) / (60*1000);
		
		final CostStrategy cs = CostStrategy.getInstance(getClass());
		
		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>() {

			@Override
			public boolean travel(DataTraveler<PriceEx> item) {
				if( item.data.actual > 0 ) {
					OrderItem oi = new OrderItem();
					
					oi.id = item.data.id;
					oi.cost = cs.getItemCost(item.data, WSOrderImpl.this);
					Integer qty  = load.get(oi.id);
					if( qty != null )
						oi.qty = qty;
	
					data.items.add(oi);
				}
				return true;
			}
		}, null);

		Agents a = (Agents) AgentPrefix.get();
		if( a != null )
			data.agentSklad = a.sklad;
		write();
		return true;
//		long r = find(data.created);
//		
//		if(r == ExtrasConst.INVALID_ID){
//			fill();
//			write();
//		}else
//			read(r);
//		
//		close();
//		
//		return true;
	}

//	private void fill() {
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(Util.getDate());
//		long begin = cal.getTime().getTime();
//		cal.add(Calendar.DAY_OF_MONTH, 1);
//		long end = cal.getTime().getTime();
//
//		Sales sls = new Sales();
//		DbReader r = new DbReader();
//		String tbl = DataObjectInfo.getInstance().getTableName(Sales.class);
//		boolean bdo = r.select(sls, tbl, "created >= " + begin + " and created < " + end);
//
//		HashMap<String, ArrayList<Integer>> ordItems = new HashMap<String, ArrayList<Integer>>();
//
//		while (bdo) {
//				for (OrderItem i : sls.items) {
//					ArrayList<Integer> qtyList = null;
//
//					if (ordItems.containsKey(i.id))
//						qtyList = ordItems.get(i.id);
//					else {
//						qtyList = new ArrayList<Integer>();
//						ordItems.put(i.id, qtyList);
//					}
//
//					qtyList.add(i.qty);
//				}
//
//			bdo = r.selectNext(sls);
//		}
//
//		for (Map.Entry<String, ArrayList<Integer>> e : ordItems.entrySet()) {
//			int qty = 0;
//			for (Integer i : e.getValue())
//				qty += i;
//
//			if (qty > 0) {
//				OrderItem item = new OrderItem();
//				item.qty = qty;
//				item.id = e.getKey();
//
//				data.items.add(item);
//			}
//		}
//
//		r.close();
//	}
	
	static public long find(Date d) {
		long ret = ExtrasConst.INVALID_ID;
		long from, to;
		from = d.getTime();
		
		// перейдем на начало дня
		from -= (from % (1000 * 3600 * 24));
		
		// начало следующего дня
		to = from + (1000 * 3600 * 24);
		String tn = DataObjectInfo.getInstance().getTableName(WSOrder.class);
		String condition = "created >= " + Long.toString(from) + " AND created < " + Long.toString(to);
		DbWriter.checkDBTable(getDataType(WSOrder.class));
		List<Long> ids = DbReader.readIds(tn, condition, null);
		
		if( ids.size() > 0 )
			ret = ids.get(0);
		return ret;
	}
}
