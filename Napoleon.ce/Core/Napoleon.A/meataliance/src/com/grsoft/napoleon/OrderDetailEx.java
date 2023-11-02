package com.grsoft.napoleon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class OrderDetailEx extends OrderDetail {
	@Override
	protected Dialog createFocusWarningDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(R.string.del_order_warning);
		builder.setPositiveButton(R.string.delete, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				doc.delete();
				finish();
				
			}
		});
		builder.setNegativeButton(R.string.close, null);
		return builder.create();
	}
	
	protected void setAdapter(){
		lvItems.setAdapter(new OrderItemsAdapter(){
			@Override
			int getResourceID() { return R.layout.orderdetail_list_rowex; }
		});
	}

}
