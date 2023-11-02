/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Объект данных Remnants для работы с базой
 *
 * kki   11/04/2011   creating
 */
package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.CreateDocDataObject;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.RemnantItem;
import com.grsoft.dataobjects.Remnants;
import com.grsoft.napoleon.Features;
import com.grsoft.napoleon.InputNumberDlg;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.RemnantsDetail;
import com.grsoft.napoleon.Warehouse;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.Itemsable;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.InputNumber;
import com.grsoft.util.Util;

import android.content.Context;

public class RemnantsImpl extends CreatableDocument<Remnants> 
	implements Itemsable {
	
	@SuppressWarnings("deprecation")
	@Override
	public CreatableDocument<Remnants> copy() {
		RemnantsImpl result = null;
		
		if (rowid != ExtrasConst.INVALID_ID)
		{
			result = new RemnantsImpl();
			result.read(rowid);
			result.data.date = Util.getDateTime();
			result.data.flags = 0;
			result.data.params = 0;
			result.rowid = ExtrasConst.INVALID_ID;
			result.write();
		}
		
		return result;
	}
	
	@Override
	public boolean isEmpty() {
		return data.items == null || data.items.size() == 0;
	}

	/**
	 * Ищем документ остатков на дату - по одной точке мы можем создать только один документ остатков в день
	 * @param d
	 * @return
	 */
	static public long find(String orgId, Date d) {
		long ret = ExtrasConst.INVALID_ID;
		
		if(orgId != null && d != null){
			long from, to;
			from = Util.getDayStart(d).getTime();
			
			// перейдем на начало дня
//			from -= (from % (1000 * 3600 * 24));
			
			// начало следующего дня
			to = from + (1000 * 3600 * 24);
			String tn = DataObjectInfo.getInstance().getTableName(Remnants.class);
			String condition = "id='" + orgId + "' AND date >= " + Long.toString(from) + " AND date < " + Long.toString(to);
			DbWriter.checkDBTable(getDataType(Remnants.class));
			List<Long> ids = DbReader.readIds(tn, condition, null);
			
			if( ids.size() > 0 )
				ret = ids.get(0);
		}
		
		return ret;
	}
	
	/**
	 * Инициализирует, но не записывает документ
	 * @param refDoc
	 */
	public void init(Document<?> refDoc) {
		data.id = refDoc.getId();
		CreateDocDataObject o = (CreateDocDataObject) refDoc.getData();
		// Добавим секунду к заявке
		data.date = new Date(o.created.getTime() + 1000);
		data.latitude = o.latitude;
		data.longitude = o.longitude;
		data.created = Util.getDateTime();
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord coord) {
		Date dt = Util.getDateTime();
		long r = find(orgId, dt);
		
		if( r != ExtrasConst.INVALID_ID )
			read(r);
		else {
			super.init(context, orgId, coord);
			r = getRowid();
		}
		
		if (r != ExtrasConst.INVALID_ID) {
			DocType.setCurDoc(RemnantsDoc.instance());
			openPrice(context);
		}
		return false;
	}
	
	public boolean baseInit(Context context, String orgId, GpsCoord coord){
		return super.init(context, orgId, coord);
	}
	
	@Override
	public void postInit() {
		data.date = data.created;
	}

	protected void openPrice(Context context){
		Warehouse.open(context, this, false);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isExported() { 
		if ((data.flags & ParamState.ofExported) == ParamState.ofExported)
			return true;
		else
			return super.isExported();
	}

	@Override
	public Date getDate() { return data.date; }

	@Override
	public String getDescription(Context context) {
		return (isExported()) ? context.getString(R.string.sent) : ""; 
	}

	@Override
	public String getId() {return data.id; }

	@Override
	public void open(Context context) { RemnantsDetail.open(context, this); }

	@Override
	public void editItem(final long itemRowid, final Context context) { 
		InputNumberDlg.open(context, new InputNumber() {
			
		@Override public boolean useComma() { return !Features.INTEGER_INPUTS_QTY; }
		@Override public boolean replaceCommaToPlus() { return Features.REPLACE_COMMA_TO_PLUS; }
		
		@Override
		public void applayInput(int value, Object... params) {
			
			if (isExported())
				return;
			
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			
			boolean refresh = false;
			if( value == 0 && editValue.length() == 0) {
				refresh = deleteItem(priceImpl.getData());
			} else 
				if( Features.REST_IN_PACK )
					value = (int)((long)value * priceImpl.getData().qtyInPack / Consts.QTY_SCALE);
				refresh = updateQty(priceImpl, value, 0, false);
			if (refresh && context instanceof DataSetNotify)
				((DataSetNotify)context).notifyDataSetChanged();
			
			priceImpl.close();
			
			RemnantsDoc.instance().refreshDocSum(data.id);
		}

		@Override
		public int getValue() {
			PriceImpl priceImpl = new PriceImpl();
			priceImpl.read(itemRowid);
			priceImpl.close();
			RemnantItem ri = (RemnantItem)findItem(priceImpl.data.id);
			int qty = ri == null ? 0 : ri.qty;
			
			if( Features.REST_IN_PACK )
				qty = (int)((long)qty * Consts.QTY_SCALE / priceImpl.getData().qtyInPack);
			return qty;
		}
	});}

	@Override
	public DataObject findItem(String itemId) {
		
		if( data.items != null )
			for(RemnantItem ri : data.items) {
				if( ri.id.compareTo(itemId) == 0 )
					return ri;
			}
		
		return null;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost, boolean inPack) {	
		Price price = priceImpl.getData();
		RemnantItem item = (RemnantItem) findItem(price.id);

		boolean needUpdate = true;
		if( item == null ) // new item
		{
			if( qty >= 0 )
			{
				Class <? extends DataObject> itemClass = DataObjectInfo.getInstance().getListType(data.getClass(), "items");

				try {
					item = (RemnantItem) itemClass.newInstance();
					
					item.id = price.id;
					item.qty = qty;
					data.items.add(item);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else
				needUpdate = false;
		} else
		{
//			if( qty == 0 )
//				data.items.remove(item);
//			else {
				if( item.qty != qty )
					item.qty = qty;
				else
					needUpdate = false;
//			}
		}
		
		if( needUpdate )
			write();
		
		return needUpdate;
	}

	public boolean deleteItem(Price item) {
		boolean result = false;
		DataObject ditem = findItem(item.id);
		
		if(ditem != null){
			data.items.remove(ditem);
			write();
			result = true;
		}
		
		return result;
	}
	
	@Override
	public int getItemColor() { return R.color.magneta; }

	@Override
	public int getItemValue(Price item) { 
//		if( Features.QTY_IN_PACK_IN_DOCS &&((CfgNpl)ConfigManager.getConfig()).isPackView )
//			return (int)((long)item.qty * Consts.QTY_SCALE / item.qtyInPack);

		return item.qty;
	}

	@Override
	public int getItemQty(Price item) {
		RemnantItem ri = (RemnantItem) findItem(item.id);		
		return ri == null ? 0 : ri.qty;
	}

	@Override
	public long getItemSum(Price item) {
		return 0;
	}
	
	@Override
	public int qty() {
		int count = 0;
		for(RemnantItem oi : data.items)
			count += oi.qty;
		return count/Consts.QTY_SCALE;
	}
}
