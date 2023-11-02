package com.grsoft.dataobjects.impl;

import java.util.ArrayList;
import java.util.List;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Fridge;
import com.grsoft.dataobjects.InvFrg;
import com.grsoft.dataobjects.InvFrgItem;
import com.grsoft.napoleon.InvFrgEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;
import android.content.Context;


public class InvFrgImpl extends CreatableDocument<InvFrg> {

	@Override public void open(Context context) { InvFrgEdit.open(context, getRowid());	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		boolean result = false;
	
		final List<Fridge> list = new ArrayList<Fridge>();
		DataTraveler.travel(Fridge.class, new DataTraveler.Travel<Fridge>(true) {

			@Override
			public boolean travel(DataTraveler<Fridge> item) {
				list.add(item.data);
				return true;
			}}, "id='" + orgId + "'");
		
		for(Fridge f : list){
			InvFrgItem i = new InvFrgItem();
			i.number = f.number;
			data.items.add(i);
		}
		
		result = data.items.size() > 0;
		
		if(result)
			result = super.init(context, orgId, gpsCoord); 
		
		return result; 
	}
}
