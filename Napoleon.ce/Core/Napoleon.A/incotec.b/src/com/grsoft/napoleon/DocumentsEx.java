package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.Util;

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
						CreateOrder.forceOpen(DocumentsEx.this, oi, false);
					}						
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected String orgInfo(Org o) {
		String ret = super.orgInfo(o);
		int balance = ((OrgEx)o).balance;
		if(balance > 0)
			ret += "<br/>Долг контрагента: <b>" + Util.IntToScaleStr(balance, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
		return ret;
	}
	
	public void askForOrderCreation(long rid) {
		orderRowId = rid;
		showDialog(ORDER_CREATION_DIALOG);
	}
}
