package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DNum;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.network.ObjectExportListener;
import com.grsoft.util.Util;

public class DocNumberHitching extends Hitching implements ObjectExportListener {
	List<DNum> data = new ArrayList<DNum>();
	
	public DocNumberHitching(boolean toSend) {
		super(DNum.class, "SalesDocNumbers");
		
		if(toSend) {
			String where = "date >= " + Long.toString(Util.getDate().getTime());
			DataTraveler.travel(DNum.class, new DataTraveler.Travel<DNum>(true) {

				@Override
				public boolean travel(DataTraveler<DNum> item) {
					data.add(item.data);
					return true;
				}
			}, where);
		}
	}

	@Override public int size() { return data.size(); }
	@Override public DataObject get(int i) { return data.get(i); }
}
