package com.grsoft.napoleon;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected void init() {
		btnSend.setEnabled(((OrderEx)doc.getData()).fromKIS == 0);
	}

	@Override
	public void send() {
		List<DocExportListener> docs = new ArrayList<DocExportListener>();
		
		docs.add(new DocSendListner(docType.getObjectName(), doc, doc.getRowid()));
		
		long rid = RemnantsImpl.find(doc.getId(), doc.getData().created);
		if( rid != ExtrasConst.INVALID_ROWID) {
			RemnantsImpl ri = new RemnantsImpl();
			ri.read(rid);
			if(ri.isExported() == false)
				docs.add(new DocSendListner(RemnantsDoc.instance().getObjectName(), ri, ri.getRowid()));
			ri.close();
		}
		
		new DocumentSender(OrderDetailEx.this, btnSend, docs).execute((Void[])null);
	}

	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}


	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter() {
			int getResourceID() { return R.layout.orderdetail_list_rowex; }

			@Override
			public View getView(int pos, View view, ViewGroup arg2) {
				view = super.getView(pos, view, arg2);

				if (DocType.getCurDoc() == OrderDoc.instance()) {
					OrderItem item = (OrderItem) getItem(pos);
					long ren = ((OrderImplEx)doc).getRentability(item.id);
					TextView tv = (TextView) view.findViewById(R.id.tvRen);
					tv.setText(Util.IntToScaleStr(ren, Consts.SUM_SCALE));
				}

				return view;
			}
		});
	}
}
