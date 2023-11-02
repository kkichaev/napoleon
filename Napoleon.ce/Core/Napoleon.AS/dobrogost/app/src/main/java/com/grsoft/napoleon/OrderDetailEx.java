package com.grsoft.napoleon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.grsoft.dataobjects.AgentGroupPlan;
import com.grsoft.dataobjects.AgentNeedSell;
import com.grsoft.dataobjects.AgentProcent;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrderDetailEx extends OrderDetail implements SendResultListener {
	AgentNeedSell needMoreSell;

	boolean doBack = false;
	EditText editOutRemark;

	@Override
	protected String getOrgText(Org o) {
		String ret = super.getOrgText(o);
		if( o.isStopList() )
			ret = "ЗАБЛОКИРОВАН!\n" + ret;
		return ret; 
	}
	
	boolean isDocValid() {
		boolean ret = true;
		if(doc.isEditable()) {
			OrgEx oe = (OrgEx)org.getData();
			if(oe.checkItemGroups != 0 ) {
				AgentGroupPlan plan = AgentGroupPlan.getPlan();
				if( plan != null ) {
					needMoreSell = plan.check((OrderEx)doc.getData());
					if(needMoreSell.needMoreSell()) {
						ret = false;
						((OrderEx)doc.getData()).outOrder = 1;
						showDialog(R.id.need_order_dlg);
					} else {
						OrderEx order = (OrderEx)doc.getData();
						order.outOrderRemark = "";
						order.outOrder = 0;
					}
					doc.write();
				}
			}
		}
		return ret;
	}
	
	void doNextStep() {
		if(doBack)
			OrderDetailEx.super.onBackPressed();
		else
			OrderDetailEx.super.send();
	}

	@Override protected void setContentView() { setContentView(R.layout.orderdetailex); }

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.need_order_dlg) {			
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Предупреждение");
			b.setMessage("");
			b.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
					
					if(OutOrderHelper.needExplain()) {
						showDialog(R.id.out_order_explain);
					} else {
						doNextStep();
					}
				}
			});
			
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					ItemGroupOrder.open(OrderDetailEx.this, doc);
				}
			});
			return b.create();
		}
		
		if(id == R.id.out_order_explain) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Объяснительная");
			View v = View.inflate(this, R.layout.explan_edit, null);
			editOutRemark = (EditText)v.findViewById(R.id.edText);
			editOutRemark.setOnLongClickListener(new View.OnLongClickListener() {
				
				@Override
				public boolean onLongClick(View arg0) {
					showDialog(R.id.choose_out_order_explain);
					return false;
				}
			});
			
			b.setView(v);
			b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { dialog.dismiss();}
			});
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { 
					dialog.dismiss();
					OrderEx oe = (OrderEx)doc.getData();
					oe.outOrderRemark = editOutRemark.getText().toString();
					if(oe.outOrderRemark.length() > 0) {
						doc.write();
						doNextStep();
					}
				}
			});
			
			return b.create();
		}
		
		if( id == R.id.choose_out_order_explain) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Выберите вариант");
			
			final CharSequence[] values = OutOrderHelper.outOrderCause(); 
			b.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					editOutRemark.setText(values[which]);
					dialog.dismiss();
				}
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		if( id == R.id.need_order_dlg) {
			String message = "Вы недозаказали ";
			if(needMoreSell.needSell > 0) {
				message += " еще " + Integer.toString(needMoreSell.needSell) + " групп ";
			}
			message += " общим весом " + Util.IntToScaleStr(needMoreSell.weight, Consts.WEIGHT_SCALE) + " кг\nДозаказать?";			
			((AlertDialog)dialog).setMessage(message);
		} else if( id == R.id.out_order_explain) {
			editOutRemark.setText(((OrderEx)doc.getData()).outOrderRemark);
		} else
			super.onPrepareDialog(id, dialog);
	}	
	@Override
	protected void onResume() {
		super.onResume();
		updatePrcInfo(this, (OrderEx) doc.getData());
	}
	
	public static void updatePrcInfo(Activity a, OrderEx o) {
		double prc = AgentProcent.getProcent() / (100.0 * Consts.SUM_SCALE);
		double sum = o.sum() * prc;
		
		TextView tv = (TextView) a.findViewById(R.id.tvPrcInfo);
		if( sum == 0 )
			tv.setVisibility(View.GONE);
		else {
			tv.setVisibility(View.VISIBLE);
			String earn = a.getString(R.string.your_earn);
			String text = String.format(earn, Util.IntToScaleStr((long)(sum * Consts.SUM_SCALE), Consts.SUM_SCALE, Util.DEC_DELIM, false ));
			if( tv != null) {
				tv.setText(text);
				a.findViewById(R.id.llPrcInfo).setVisibility(View.VISIBLE);
			}
		}
			
	}
	
	@Override
	public void onBackPressed() {
		doBack = true;
		if(isDocValid())
			super.onBackPressed();
	}
	
	@Override
	public void send() {
		doBack = false;
		if( isDocValid() )
			new DocumentSender(OrderDetailEx.this, btnSend, 
					DocType.getCurDoc().getObjectName(), doc, 
					doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	public void postSendExecute(boolean result) {
		OrgImpl org = new OrgImpl();
		org.getData().id = doc.getId();
		org.read();
		org.close();
				
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(getOrgText(org.getData()));
	}
	
	@Override
	protected void setAdapter() {
		lvItems.setAdapter(new OrderItemsAdapterEx());
	}
	
	@Override
	protected void drawItemQty(int color, OrderItem item, TextView tvQty) {
		String qtyText;
		qtyText = Util.IntToScaleStr(item.qty, Consts.QTY_SCALE);
		
		qtyText += ((OrderItemEx)item).inKG > 0 ?  " кг." : " шт.";
		
		tvQty.setText(qtyText);
	}
	
	class OrderItemsAdapterEx extends OrderItemsAdapter {
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item) {
			OrderItemEx ie = (OrderItemEx)item;
			super.drawInternal(view, name, (ie.remark == null || ie.remark.length() == 0) ? color : Color.GREEN, item);
		}
	}
}
