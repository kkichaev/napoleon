package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;
import com.grsoft.util.Util;

@TableInfo(name="stc_goods", keyFields="date")
@ServerInfo(name="StorcheckGoods")
public class StorcheckGoods extends DataObject {
	public static final int NEW_GOODS_FOLDER = 1;
	public static final int TOP_30_FOLDER = 2;
	
	public Date date;
	
	public List<SCGoodItem> items = new ArrayList<SCGoodItem>();

	public static StorcheckGoods get(Date date) {
		TrvlG t = new TrvlG(Util.getDayStart(date));
		DataTraveler.travel(StorcheckGoods.class, t, "", "date");
		return t.result();
	}
}

class TrvlG extends DataTraveler.Travel<StorcheckGoods> {
	StorcheckGoods ret = null;
	Date date;
	
	public TrvlG(Date d) {
		super(true);
		this.date = d;
	}

	@Override
	public boolean travel(DataTraveler<StorcheckGoods> item) {
		if(item.data.date.compareTo(date) <= 0) {
			ret = item.data;
			return true;
		}
		return false;
	}
	
	public StorcheckGoods result() { return ret; }
}
