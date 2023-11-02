package com.grsoft.napoleon;

import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.RivalPrice;
import com.grsoft.dataobjects.impl.MonitoringImpl;
import com.grsoft.dataobjects.impl.MonitoringImplBase;
import com.grsoft.dataobjects.impl.RivalMonitoringImpl;
import com.grsoft.dataobjects.impl.RivalPriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.RivalMntrDoc;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;

public class RivalMonitoringDetail extends MonitoringDetail {
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, RivalMonitoringDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected MonitoringImplBase<?> createDoc() {
		return new RivalMonitoringImpl();
	}

	
	@Override
	protected void editItem(MonitoringItem item) {
		RivalPriceImpl pi = new RivalPriceImpl();
		RivalPrice prc = pi.getData();
		prc.id = item.id;
		pi.read();
		pi.close();
		doc.editItem(pi.getRowid(), this);
	}
	
	@Override
	protected MonitoringDetailAdapter createAdapter(Context ctx) {
		return new RivalDetailAdapter(ctx);
	}
	
	@Override
	public void send() {
		new DocumentSender(this, btnSend, 
				RivalMntrDoc.getCurDoc().getObjectName(), doc, 
					doc.getRowid(), this).execute((Void[])null);
	}
}
