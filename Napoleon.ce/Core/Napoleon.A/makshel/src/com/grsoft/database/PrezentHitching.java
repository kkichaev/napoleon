package com.grsoft.database;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.PresentEx;

public class PrezentHitching extends ReportHitching {
	public PrezentHitching() {
		super("update_prezent", new ReportParam(), new UpdatePrezentHitching());
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

class ReportParam extends DataObject{
	public List<Item> items;
	
	public ReportParam() {
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