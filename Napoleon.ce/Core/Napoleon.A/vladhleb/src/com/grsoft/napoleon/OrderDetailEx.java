package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.network.DocExportListener;

public class OrderDetailEx extends OrderDetail {
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.ask_send_docs ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Отправка документов");
			b.setMessage("Отправить текущую или отправить все неотправленные?");

			b.setPositiveButton("Все", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					DocExportListener dl = OrderDoc.instance().getDirtyDocuments();
					List<DocExportListener> list = new ArrayList<DocExportListener>();
					list.add(dl);
					new DocumentSender(OrderDetailEx.this, btnSend, list).execute((Void[])null);
				}
			});
			
			b.setNegativeButton("Текущую", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) { OrderDetailEx.super.send(); }
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void send() {
		
		DocExportListener dl = OrderDoc.instance().getDirtyDocuments();
		if( dl.getDocuments().getCount() > 1) {
			showDialog(R.id.ask_send_docs);
			return;
		}
		
		super.send();
	}
}
