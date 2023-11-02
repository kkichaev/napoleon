package com.grsoft.dataobjects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.grsoft.database.ServerInfo;
import com.grsoft.database.TableInfo;

@TableInfo(name="stc_actions", keyFields="date")
@ServerInfo(name="StorcheckActions")
public class StorcheckActions extends DataObject {
	public Date date;

	public List<SCActionItem> items = new ArrayList<SCActionItem>();
	
	public static StorcheckActions get(Date date) {
		TrvlA t = new TrvlA(date);
		DataTraveler.travel(StorcheckActions.class, t, "", "date");
		return t.result();
	}
}

class TrvlA extends DataTraveler.Travel<StorcheckActions> {
	StorcheckActions ret = null;
	Date date;
	
	public TrvlA(Date d) {
		super(true);
		this.date = d;
	}

	@Override
	public boolean travel(DataTraveler<StorcheckActions> item) {
		if(item.data.date.compareTo(date) <= 0) {
			ret = item.data;
			return true;
		}
		return false;
	}
	
	public StorcheckActions result() { return ret; }
}
