package com.grsoft.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.OrderRequest;
import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.OrderRequestImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.network.ObjectListener;

public class SendOrderRequest {
	private UpdateCtrl updctrl;
	private Context context;
	
	public SendOrderRequest(Context context, UpdateCtrl updctrl){
		this.context = context;
		this.updctrl = updctrl;
	}
	
	public void execute(){
		DocSendListner docs = new DocSendListner(DataObjectInfo.getInstance().getSrvName(OrderRequest.class), 
				OrderRequestImpl.class, "params", ParamState.ofExported);
		UpdateProcess process = new UpdateProcess((Activity) context, updctrl, null);
		
		List<ObjectListener> list = new ArrayList<ObjectListener>();
		list.add(docs);
		process.setSending(list);
		process.execute((Void[])null);
	}
}
