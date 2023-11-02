package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;

import android.app.Dialog;
import android.content.DialogInterface;

public class ScriptEditEx extends ScriptEdit {
	private int curpos = -1;
	
	@Override
	protected void openDoc(int position) {
		ScriptDefItem i = def.getData().items.get(position);
		OrgImpl oi = new OrgImpl();
		oi.getData().id = doc.getId();
		oi.read();
		oi.close();
		OrgEx org = (OrgEx)oi.getData();
		DeliveryInfo di = DeliveryInfo.collectDelivery(doc.getId());
		
		if(i.curType.equals(OrderDoc.instance().getObjectName()) &&	(di.hasExceed || di.sum >= org.limitsum)) {
			curpos = position;
			showDialog(R.id.has_exceed_delivery_dlg);
		}else
			super.openDoc(position);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.has_exceed_delivery_dlg) {
			return new ExceedDeliveryDialogFactory().createDialog(this, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					docOpenning(curpos);
				}
			});
		}else
			return super.onCreateDialog(id);
	}
}
