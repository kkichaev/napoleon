package com.grsoft.dataobjects.impl;

import java.util.List;

import com.grsoft.database.DbReader;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.Bonus;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.PriceCountBonus;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.GpsCoord;

import android.content.Context;

public class BonusImpl extends OrderImplBase<Bonus>{
	int whIndex = -1; 
	
	@Override
	public void open(Context context) {
		
	}
	
	public static BonusImpl findOrCreate(Context context, Order order, String action) {
		BonusImpl result = new BonusImpl();
		long rowid = find(order, action);
		
		if(rowid == ExtrasConst.INVALID_ROWID) {
			result.init(context, order.id, new GpsCoord(order.latitude, order.longitude, order.stltime));
			result.data.order = order.created;
			result.data.whNumber = ((OrderEx)order).whNumber;
			result.data.action = action;
			result.write();
		}
		else 
			result.read(rowid);
		
		result.close();
		
		return result;
	}

	private static String getWhere(Order order, String action) {
		return String.format("[order] = %d and action = '%s'", order.created.getTime(), action);
	}
	
	public static long find(Order order, String action) {
		long result = ExtrasConst.INVALID_ROWID;
		
		DbWriter.checkDBTable(Bonus.class);
		List<Long> ids = DbReader.readIds(DataObjectInfo.getInstance().getTableName(Bonus.class), getWhere(order, action), null);
		
		if (ids.size() > 0) {
			result = ids.get(0);
			
			BonusImpl b = new BonusImpl();
			b.read(result);
			
			if (b.data.items.size() == 0) {
				b.delete();
				result = ExtrasConst.INVALID_ROWID;
			}
			
			b.close();
		}
		
		return result;
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		PriceCountBonus.open(context, itemRowid, this);
	}

	@Override
	public void editProperties(Context ctx, boolean isOldOrder) {
		
	}

	@Override
	public CreatableDocument<Bonus> createInstance() {
		return new BonusImpl();
	}
	
	@Override
	public int getItemValue(Price item) {
		if( whIndex == -1 ) 
			whIndex = getWhIndex();

		if( whIndex == 0 )
			return item.qty;
		
		return (whIndex <= ((PriceEx)item).whQty.size()) ? 
				((PriceEx)item).whQty.get(whIndex-1).qty : 
				0;
	}
	
	public int getWhIndex() {
		return data.whNumber;
	}
	
	@Override
	public String getDescription(Context context) {
		String result = super.getDescription(context);
		String aname = context.getResources().getString(R.string.action_not_found, data.action);
		ActionImpl a = new ActionImpl();
		
		if(a.read("id",data.action))
			aname = a.data.name;
		
		result += "<br><b><i>" + aname + "</i></b>";
		
		return result;
	}
}
