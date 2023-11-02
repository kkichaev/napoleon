package com.grsoft.napoleon;

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.grsoft.dataobjects.FocusedGroupItem;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImplClassic;

public class OrderDetailClassic extends OrderDetail {
	
	@Override
	protected boolean haveFocusedGroup() {
		return canCheckFocusedItems();
	}
	
	@Override protected boolean disableSendWithoutFocusedGroup() { return false; }
	
	@Override
	protected boolean haveUnsettedFocusedGroups() {
		if( doc instanceof OrderImplClassic ) {
			List<FocusedGroupItem> items = ((OrderImplClassic)doc).getUnsettedFocusedItems();
			return items.size() > 0;
		}
		return super.haveUnsettedFocusedGroups();
	}
	
	@Override
	protected void send() {
		if( doc.isExported() == false && (doc.getData().params & OrderImplClassic.AUTOORDER)  != 0 ) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Подтверждение");
			b.setMessage("Отправить автозаказ?");
			b.setPositiveButton("Да", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					OrderDetailClassic.super.send();
					dialog.dismiss();
				}
			});
			b.setNegativeButton("Нет", null);
			b.create().show();
			return;
		}

		super.send();
	}
	
	boolean canCheckFocusedItems() {
		ConfigImpl c = new ConfigImpl();
		c.getData().key = "CheckFocusedItems";
		boolean focusedItems = (c.read() && Integer.parseInt(c.getData().value) == 1);
		c.close();
		
		return focusedItems;
	}
}
