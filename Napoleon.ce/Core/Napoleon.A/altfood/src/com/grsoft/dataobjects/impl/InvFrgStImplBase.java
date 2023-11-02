package com.grsoft.dataobjects.impl;

import java.util.Date;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.InvFrgStBase;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.InvFrgStEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;
import com.grsoft.util.GpsCoord;
import com.grsoft.util.Util;
import android.content.Context;

public class InvFrgStImplBase<T extends InvFrgStBase> extends CreatableDocument<T>
implements PhotoDocument{

	@Override
	public void open(Context context) {
		InvFrgStEdit.open(context, getRowid());
	}
	
	public boolean init(Context context, String orgId, GpsCoord gpsCoord, Date invfrg) {
		data.invfrg = invfrg;
		return super.init(context, orgId, gpsCoord);
	}

	@Override
	public void addPhoto(byte[] photo) {
		try{
			@SuppressWarnings("unchecked")
			Class<? extends VisitItem> itemType = (Class<? extends VisitItem>) DataObjectInfo.getInstance().getListType(Visit.class, "items");
			VisitItem visitItem = itemType.newInstance();
			visitItem.id = photo;
			getData().items.add(visitItem);
			write();
			close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override public int count() { return data.items.size(); }
	
	@Override
	public void postInit() {
		data.date = Util.getDateTime();
	}

}
