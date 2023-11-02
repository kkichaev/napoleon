package com.grsoft.dataobjects.impl;

import com.grsoft.dataobjects.DWaybillDocument;
import com.grsoft.dataobjects.DWaybillDocumentItem;
import com.grsoft.dataobjects.Dispatch;
import com.grsoft.dataobjects.DispatchDocDataObject;
import com.grsoft.dataobjects.DispatchItem;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.util.GpsCoord;
import android.content.Context;

public abstract class  DispatchDocImpl <T extends DispatchDocDataObject> extends CreatableDocument<T> {
	
	public boolean init(Context context, DispatchImpl doc, DispatchItem i, GpsCoord loc){
		Dispatch srcDoc = doc.getData();
		data.dispatch = srcDoc.created;
		data.routeItemId = srcDoc.itemid;
		
		if(i != null){
			data.disprem = i.remark;
			data.number = i.number;
		}
		
		return super.init(context, doc.getId(), loc);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		data.params |= Dispatch.NOT_READY_TO_SEND;
	}
	
	@Override
	public boolean delete() {
		DispatchDocUtil.delete(this);
		return super.delete();
	}

	public void setReadyToSend() {	data.params &= ~Dispatch.NOT_READY_TO_SEND;	}
	
	@Override
	public boolean isEditable() { return true; /*!isReadyToSend() && super.isEditable(); */ }

	protected boolean isReadyToSend() {	return !((data.params & Dispatch.NOT_READY_TO_SEND) == Dispatch.NOT_READY_TO_SEND);	}
	
	public void setRejected() {
		setReadyToSend();
		
		data.params |= Dispatch.REJECTED;
		if(data instanceof DWaybillDocument) {
			for(DWaybillDocumentItem i : ((DWaybillDocument)data).items) {
				i.outqty = 0;
			}
		}
	};
}
