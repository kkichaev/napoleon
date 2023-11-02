package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PresentEx;
import android.content.Context;

public class PrezentHitching extends ReportHitching {
	public PrezentHitching(Context context) {
		super("update_prezent_dostavka", new ReportParam(context), new UpdatePrezentHitching(context));
	}
}

class ReportParam extends DataObject{
	public List<Item> items;
	
	public ReportParam(Context context) {
		StoreUtils.initPresentation(context);
		items = new ArrayList<Item>();
		
		DataTraveler.travel(PresentEx.class, new DataTraveler.Travel<PresentEx>() {

			@Override
			public boolean travel(DataTraveler<PresentEx> item) {
				items.add(new Item(item.data));
				return true;
			}
		}, null);
	}
}

class Item extends DataObject {
	public String photoPath = "";
	public String crc = "";
	
	public Item(PresentEx src) {
		photoPath = src.photoPath;
		crc = src.crc;
	}
}