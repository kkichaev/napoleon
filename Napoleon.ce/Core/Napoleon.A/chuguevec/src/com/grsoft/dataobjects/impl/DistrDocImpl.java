package com.grsoft.dataobjects.impl;

import java.util.ArrayList;

import android.content.Context;

import com.grsoft.dataobjects.DistrDoc;
import com.grsoft.dataobjects.DistrItem;
import com.grsoft.dataobjects.MatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.DistrEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;

public class DistrDocImpl extends CreatableDocument<DistrDoc> {

	@Override
	public void open(Context context) {
		DistrEdit.open(context, this);
	}

	@Override
	public boolean init(Context context, String orgId, GpsCoord gpsCoord) {
		super.init(context, orgId, gpsCoord);
		
		OrgImpl oi = new OrgImpl();
		Org o = oi.getData();
		o.id = orgId;
		oi.read();
		
		data.name = o.name;
		
		data.items = new ArrayList<DistrItem>();
		
		TypeOrgMatrixImpl mtx = new TypeOrgMatrixImpl();
		mtx.getData().id = ((OrgEx)o).mid;
		
		PriceImpl price = new PriceImpl();
		int i = 1;
		if(mtx.read()){
			for(MatrixItem mi : mtx.data.items){
				price.getData().id = mi.id;
				if(price.read())
					data.items.add(new DistrItem(i++, mi.id, price.getData().name));
			}
		}
		
		mtx.close();
		price.close();
		
		write();
		close();
		
		return true;
	}
}
