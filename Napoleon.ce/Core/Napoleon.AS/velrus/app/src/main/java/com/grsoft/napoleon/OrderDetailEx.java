package com.grsoft.napoleon;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrderImplEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import java.util.HashSet;

public class OrderDetailEx extends OrderDetail {

	ActionHelper actionHelper = new ActionHelper();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(actionHelper.applyToAll().size() > 0) {
			View ab = findViewById(R.id.btnAction);
			ab.setVisibility(View.VISIBLE);
			ab.setOnClickListener(v -> {
				actionHelper.showApplyToAll(OrderDetailEx.this, doc);
			});
		}
	}

	@Override protected void setContentView() { setContentView(R.layout.orderdetailex); }
	
	@Override
	public void onBackPressed() {
		if(((OrderEx)doc.getData()).notcomplete > 0)
			Toast.makeText(this, R.string.required_items_missed, Toast.LENGTH_SHORT).show();
		else{
			if( doc.getData().items.size() == 0 )
				doc.delete();
			keyBackPressed();
			finish();
		}
	}
	
	@Override
	protected void checkFocused() {
		super.checkFocused();
		
			if((Button)findViewById(R.id.btnFocus)!=null) {
				btnSend.setEnabled(true);
			}
	}

	@Override
	protected void deleteItem(OrderItem orderItem) {
		if(((OrderItemEx)orderItem).bonus > 0) {
			return;
		}
		super.deleteItem(orderItem);
		((OrderImplEx)doc).removeActions(new HashSet<>());
	}

	@Override
	protected void editItem(OrderItem orderItem) {
		if(((OrderItemEx)orderItem).bonus > 0) {
			return;
		}
		super.editItem(orderItem);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		int minSum;
		TextView tvMinSum = (TextView)findViewById(R.id.tvInfo1);
		tvMinSum.setVisibility(View.VISIBLE);
		
		OrgImpl o = new OrgImpl();
		o.getData().id = doc.getId();
		o.read();
		o.close();
		
		OrgEx orgEx = (OrgEx)o.getData();
		minSum = orgEx.minOrder;
		
		if( doc.getData().items != null ) {
			long sum = doc.sum();
			if( sum < minSum ) {
				String text = "Сумма заявки " + Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р. меньше " +
					Util.IntToScaleStr(minSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
				tvMinSum.setText(text);
				tvMinSum.setTextColor(Color.RED);
			} else
				tvMinSum.setVisibility(View.GONE);
				
		} else {
			tvMinSum.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void send() {
		if(((OrderEx)doc.getData()).notcomplete > 0)
			Toast.makeText(this, R.string.required_items_missed, Toast.LENGTH_SHORT).show();
		else
			super.send();
	}

	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new Adapter());
	}

	class Adapter extends OrderItemsAdapter {
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			super.drawInternal(view, name, color, item, pos);
			if(((OrderItemEx)item).bonus > 0) {
				view.setBackgroundColor(Color.LTGRAY);
			}
		}
	}
}
