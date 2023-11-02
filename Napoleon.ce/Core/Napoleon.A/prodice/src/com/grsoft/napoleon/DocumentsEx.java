package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.DebtDoc;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;

public class DocumentsEx extends Documents {

	final int ORDER_CREATION_DIALOG = 1;
	
	final CharSequence[] orderOptions = { "Сделать фото", "Создать заявку" };
	
	long orderRowId = ExtrasConst.INVALID_ID;
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == ORDER_CREATION_DIALOG && orderRowId != ExtrasConst.INVALID_ID) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите вариант");
			b.setItems(orderOptions, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if( which == 0 )
						VisitEditorEx.openBeforeOrder(DocumentsEx.this, orderRowId);
					else if(which == 1) {
						OrderImpl oi = new OrderImpl();
						oi.read(orderRowId);
						oi.close();
						CreateOrder.forceOpen(DocumentsEx.this, oi, false);
					}						
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void adjustViewForDocType(DocType docType) {
		if( docType == DebtDoc.instance() ) {
			DocType.setCurDoc(docType);
			DebetView.open(this, org.getData().id);
			finish();
		} else
			super.adjustViewForDocType(docType);
	}
	
	public void askForOrderCreation(long rid) {
		orderRowId = rid;
		showDialog(ORDER_CREATION_DIALOG);
	}
}
