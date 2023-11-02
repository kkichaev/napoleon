package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.Action;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	
	List<Action> availActions = new ArrayList<Action>();
	Adapter adapter = null;
	
	@Override protected void setContentView() { setContentView(R.layout.orderdetailex);	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		cfg.isPackView = true;
		
		findViewById(R.id.btnAction).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { onAction(); }
		});
	}
	
	protected void onAction() {
		if(!doc.isEditable())
			return;
		
//		if(!assignAction) {
//			OrderEx oe = (OrderEx) doc.getData();
//			oe.removeAction();
//			doc.write();
//			refreshActionInfo();
//			adapter.notifyDataSetChanged();
//			
//			return;
//		}
//		
		if(availActions.size() > 1)
			showDialog(R.id.choose_action_dlg);
		else if(availActions.size() > 0)
			setAction(availActions.get(0));
	}
	
	void setAction(Action a) {
		OrderEx oe = (OrderEx) doc.getData();
		oe.setAction(a);
		doc.write();

		OrderDoc.instance().refreshDocSum(doc.getId());
		refreshActionInfo();
		adapter.notifyDataSetChanged();		
	}
	
	@Override
	protected void setAdapter() {
		adapter = new Adapter();
		lvItems.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		refreshActionInfo();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.choose_action_dlg) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите акцию");
			String[] acts = new String[availActions.size()];
			PriceImpl pi = new PriceImpl();
			Price p = pi.getData();
			int idx = 0;
			for(Action a : availActions) {
				p.id = a.item;
				pi.read();
				acts[idx++] = a.name + "\n" + p.name;
			}
			pi.close();
			b.setSingleChoiceItems(acts, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					setAction(availActions.get(arg1));
					arg0.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}

	private void refreshActionInfo() {
//		Button b = (Button)findViewById(R.id.btnAction);
		TextView tv = (TextView)findViewById(R.id.tvActionText);
		View v = findViewById(R.id.llActions);
		int vsbl = View.GONE;

		OrderEx oe = (OrderEx) doc.getData();
		if(oe.action.length() > 0) {
			Action a = Action.get(oe.action, oe.actionItem);
			if(a != null) {
//				b.setText(R.string.remove_action);
				String text = a.name;
				text += ", " + Util.IntToScaleStr(oe.actionSum, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " р.";
				tv.setText(text);
				vsbl = View.VISIBLE;
			}
		} else {
			availActions = Action.availActions(oe);
			if(availActions.size() > 0) {
				vsbl = View.VISIBLE;
				tv.setText("");
//				b.setText(R.string.choose_action);
			}
		}
		
		v.setVisibility(vsbl);
		updateTotalSum();
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.order_detail_row_ex; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			super.drawInternal(view, name, color, item);
			OrderItemEx oie = (OrderItemEx)item;
			TextView tv = (TextView)view.findViewById(R.id.tvDiscount);;
			String text = "";
			if(oie.cost != oie.costWD) {
				int dsc = 10000 - oie.cost * 10000 / oie.costWD;
				text = Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false);
			}
			tv.setText(text);
		}
	}
}
