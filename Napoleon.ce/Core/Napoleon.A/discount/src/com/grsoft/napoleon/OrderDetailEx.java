package com.grsoft.napoleon;

import com.grsoft.dataobjects.ActionWithText;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.napoleon.documents.OrderDoc;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail implements ActionDialog.Actions {
	@Override
	protected void setContentView() {
		setContentView(R.layout.order_detail_ex);
	}
	
	ActionDialog.ActionDialogAdapter actionAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.btnAction).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { showDialog(R.id.actions_dlg);}
		});
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.actions_dlg) {
			ActionDialog ad = new ActionDialog();
			Dialog d = ad.create(this, this, (OrderEx)doc.getData(), false);
			actionAdapter = ad.getAdapter();
			return d;
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if(id == R.id.actions_dlg) {
			if(actionAdapter != null)
				actionAdapter.refresh((OrderEx)doc.getData()	);
		} else
			super.onPrepareDialog(id, dialog);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		updateActionText();
	}
	
	void updateActionText() {
		TextView tv = (TextView)findViewById(R.id.tvInfo);
		tv.setText(Html.fromHtml(ActionWithText.getOrderText((OrderEx) doc.getData())));
	}

	@Override
	public void selected(ActionWithText action) {
		if(doc.isEditable() && action.getAffectedItem() != null) {
			OrderEx oe = (OrderEx)doc.getData();
			oe.updateAction(action);
			doc.write();
			updateActionText();
			updateTotalSum();
			OrderDoc.instance().refreshDocSum(doc.getId());
		}		
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}

	@Override public void closing() {}
	
	class Adapter extends OrderItemsAdapter {
		@Override
		protected long getItemSum(OrderItem item) {
			long sum = super.getItemSum(item) - ((OrderItemEx)item).discount; 
			return sum;
		}
	}
}
