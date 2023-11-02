package com.grsoft.ads.dataobjects.impl;

import java.util.List;

import android.content.Context;

import com.grsoft.ads.database.OrderItem;
import com.grsoft.ads.dataobjects.AgentPrefix;
import com.grsoft.ads.dataobjects.UserOrder;
import com.grsoft.ads.documents.Addressable;
import com.grsoft.ads.documents.OrderItemsDocument;
import com.grsoft.ads.utils.ConfigReader;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;

public class UserOrderImpl extends OrderItemsDocument<UserOrder>
	implements Addressable
{
	public UserOrderImpl(){
		oldDocField = "created";
	}

	@Override
	public void open(Context context) {
		
	}

	@Override
	public List<OrderItem> getOrderItems() {
		return getData().items;
	}
	
	@Override
	public boolean init(Context context, 
			String orgId, GpsCoord gpsCoord) {
		data.number = makeDocNumber();
		data.date = Util.getDateTime();
		data.created = Util.getDateTime();
		
		return true;
	}

	@Override
	public boolean isEditable() {
		return !isExported();
	}

	@Override
	public void editItem(long itemRowid, Context context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataObject findItem(String itemId) {
		if( data.items != null )
			for(OrderItem oi : data.items) {
				if( oi.priceid.compareTo(itemId) == 0 )
					return oi;
			}
		
		return null;
	}

	@Override
	public int getItemColor() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemValue(Price itemid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemQty(String itemid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getItemSum(String itemid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean updateQty(PriceImpl priceImpl, int qty, int cost,
			boolean inPack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCity() {
		return data.city;
	}

	@Override
	public void setCity(String city) {
		data.city = city;
	}

	@Override
	public String getStreet() {
		return data.street;
	}

	@Override
	public void setStreet(String street) {
		data.street = street;
	}

	@Override
	public String getHouse() {
		return data.house;
	}

	@Override
	public void setHouse(String house) {
		data.house = house;
	}

	@Override
	public String getFlat() {
		return data.flat;
	}

	@Override
	public void setFlat(String flat) {
		data.flat = flat;
	}
	
	private String makeDocNumber() {
		String prefix = "";
		
		ConfigReader config = (ConfigReader) ConfigManager.getConfig();
		AgentPrefix ap = new AgentPrefix();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(ap.getClass());
		boolean bdo = r.select(ap, table, "login='" + config.getLogin() + "' and password='" + config.getPassword() + "'" );
		if( bdo )
			prefix = ap.prefix;
		r.close();

		int num = 1;
		UserOrder ri = new UserOrder();
		table = DataObjectInfo.getInstance().getTableName(ri.getClass());
		bdo = r.select(ri, table, null, "created desc");
		while( bdo ) {
			if( ri.number.length() == 0 ) {
				bdo = r.selectNext(ri);
				continue;
			}
			try {
				StringBuilder lastnum = new StringBuilder();
				for(char sym : ri.number.toCharArray()) {
					if( Character.isDigit(sym) )
						lastnum.append(sym);
				}
				
				if( lastnum.length() > 0 )
					num = Integer.parseInt(lastnum.toString()) + 1;
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		r.close();
		return String.format("%s%06d", prefix, num);
	}

}
