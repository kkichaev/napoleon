package com.grsoft.dataobjects.impl;

import java.util.HashSet;

import android.content.Context;

import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.Distrib;
import com.grsoft.dataobjects.DistribItem;
import com.grsoft.dataobjects.PriceEx;
import com.grsoft.napoleon.DistribEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DistribDoc;
import com.grsoft.util.ExtrasConst;

public class DistribImpl extends CreatableDocument<Distrib> {

	@Override
	public void open(Context context) {
		DistribEdit.open(context, this);
	}

	@Override
	public long write() {
		long ret = super.write(); 
		if( ret != ExtrasConst.INVALID_ROWID )
			DistribDoc.instance().refreshDocSum(data.id);
		return ret;
	}
	
	public void changePriceType(String newPT) {
		data.priceType = newPT;
		data.thermalState = "";
		
		refreshItems();
	}
	
	public void changeThState(String newThState) {
		data.thermalState = newThState;
		refreshItems();
	}

	private void refreshItems() {
		data.items.clear();
		
		final HashSet<String> usedNames = new HashSet<String>();
		
		DataTraveler.travel(PriceEx.class, new DataTraveler.Travel<PriceEx>() {

			@Override
			public boolean travel(DataTraveler<PriceEx> item) {
				if( usedNames.contains(item.data.name) == false ) {
					usedNames.add(item.data.name);
					DistribItem di = new DistribItem();
					di.id = item.data.id;
					data.items.add(di);
				}
				return true;
			}
		}, "idType='" + data.priceType + "' and thermalState='" + data.thermalState + "'", "name");
	}

	public int checkValid() {
		for( int i=0; i<data.items.size(); i++) {
			if( data.items.get(i).exists < 0 )
				return i;
		}
		return -1;
	}
}
