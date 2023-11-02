package com.grsoft.dataobjects.impl;

import java.util.Date;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.ReqNewOrg;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.VisitItem;
import com.grsoft.napoleon.ReqNewOrgEdit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.PhotoDocument;

import android.content.Context;

public class ReqNewOrgImpl extends CreatableDocument<ReqNewOrg> implements PhotoDocument{

	@Override
	public void open(Context context) {
		ReqNewOrgEdit.open(context, this);
	}
	
	@Override
	public void postInit() {
		data.date = data.created;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addPhoto(byte[] photo) {
		try{
			Class<? extends VisitItem> itemType = (Class<? extends VisitItem>) DataObjectInfo.getInstance().getListType(Visit.class, "items");
			VisitItem visitItem = itemType.newInstance();
			visitItem.id = photo;
			visitItem.date = new Date();
			
			data.items.add(visitItem);
			data.sendedPhotos = 0;
			write();
			close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public int count() { return data.items.size();	}
	
	public boolean isEmpty() {
		return data.items.size() == 0 && data.inn.length() == 0 && data.jurAddress.length() == 0 && 
				data.ogrn.length() == 0 && data.address.length() == 0;
	}

}
