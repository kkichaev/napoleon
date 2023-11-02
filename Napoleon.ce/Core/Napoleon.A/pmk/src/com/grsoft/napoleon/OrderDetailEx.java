package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderFocusedFolderPMK;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class OrderDetailEx extends OrderDetail {
	boolean isSending = false;
	
	protected void setContentView(){
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		checkFocusGroups();
	}

	private void checkFocusGroups() {
		if(doc.isEditable()) {
			OrderEx oe = (OrderEx) doc.getData();
			int outFocusGroup = (OrderFocusedFolderPMK.isOrderCompleete(oe)) ? 0 : 1;
			if(oe.outFocusGroup != outFocusGroup) {
				oe.outFocusGroup = outFocusGroup;
				doc.write();
			}
		}
	}
	
	@Override
	protected void deleteItem(OrderItem orderItem) {
		super.deleteItem(orderItem);
		checkFocusGroups();
	}
	
	@Override
	public void send() {
		OrderEx oe = (OrderEx) doc.getData();
		if(doc.isEditable() && oe.outFocusGroup > 0) {
			isSending = true;
			showDialog(R.id.no_focus_group);
			return;
		}
		
		super.send();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.no_focus_group) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Предупреждение");
			
			OrderFocusedFolderPMK plan = OrderFocusedFolderPMK.currentPlan();
			String message = "В заказе меньше " + Integer.toString(plan.count) + " видов колбасных изделий.\nДобавим?";			
			b.setMessage(message);
			
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					if(isSending)
						OrderDetailEx.super.send();
					else
						finish();
				}
			});
			
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onBackPressed() {
		OrderEx oe = (OrderEx) doc.getData();
		if(doc.isEditable() && oe.outFocusGroup > 0) {
			isSending = false;
			showDialog(R.id.no_focus_group);
			return;
		}
		super.onBackPressed();
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapter() {
			@Override
			int getResourceID() {
				return R.layout.orderdetail_list_row_ex;
			}
			
			@Override
			protected void drawInternal(View view, String name, int color, final OrderItem item) {
				super.drawInternal(view, name, color, item);
				
				CheckBox cb = (CheckBox) view.findViewById(R.id.cbDecl);
				cb.setTag(item);
				cb.setOnCheckedChangeListener(null);
				cb.setChecked(((OrderItemEx) item).decl != 0);
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (doc.isEditable()) {
							((OrderItemEx) item).decl = isChecked ? 1 : 0;
							doc.write();
							doc.close();
						}
					}
				});
			}
		});
	}
}
