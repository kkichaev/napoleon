package com.grsoft.napoleon;

import com.grsoft.dataobjects.OrderItem;
import com.grsoft.dataobjects.OrderItemEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Price;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class OrderDetailEx extends OrderDetail {
	
	String costType = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		costType = OrderHelper.getSumType(doc);
	}
	
	@Override protected void setContentView() { setContentView(R.layout.orderdetailex);}
	@Override protected void setAdapter() { lvItems.setAdapter(new Adapter()); }
	
	@Override
	public void updateTotalSum(long sum, int weight, int count) {
		long dsc = 0;
		for(OrderItem oi : doc.getData().items)
			dsc += ((OrderItemEx)oi).sumWODiscount - ((long)oi.cost * oi.qty / Consts.QTY_SCALE);
		
		String text = "сумма:<b>" + Util.IntToScaleStr(doc.sum(), Consts.SUM_SCALE, Util.DEC_DELIM, false) +
				"</b> скидка: <b>" + Util.IntToScaleStr(dsc, Consts.SUM_SCALE, Util.DEC_DELIM, false) + "</b>";
		if( weight != 0) {
			text += "<br/><i>" + OrderDoc.instance().weightToString(weight, getString(R.string.kg)) + "</i>";
		}
		TextView tv = (TextView) findViewById(R.id.tvTotalSum);
		tv.setText(Html.fromHtml(text));
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.debet_info_dialog) {
			OrgEx oe = (OrgEx) org.getData();
			long curDeb = oe.balance + doc.sum(); 

			AlertDialog.Builder b = new AlertDialog.Builder(this);
			String txt = "Превышен лимит отгрузки на " + Util.IntToScaleStr(curDeb - oe.limit, Consts.SUM_SCALE, Util.DEC_DELIM, false) + " руб.!";
			
			b.setMessage(txt);
			b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override public void onClick(DialogInterface dialog, int which) { finish(); }
			});
			b.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override public void onCancel(DialogInterface arg0) { finish(); }
			});
			
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	public void onBackPressed() {
		OrgEx oe = (OrgEx) org.getData();
		if(oe.limit > 0 && !doc.isEmpty()) {
			long curDeb = oe.balance + doc.sum(); 
			if( curDeb > oe.limit ) {
				showDialog(R.id.debet_info_dialog);
				return;
			}
		}
		super.onBackPressed();
	}
	
	class Adapter extends OrderItemsAdapter {
		@Override int getResourceID() { return R.layout.orderdetail_list_rowex; }
		
		@Override
		protected void drawInternal(View view, String name, int color, OrderItem item, int pos) {
			super.drawInternal(view, name, color, item, pos);

			TextView tv = (TextView) view.findViewById(R.id.tvCostType);
			tv.setText(costType);
			
			CostStrategyEx cs = (CostStrategyEx)CostStrategy.defaultInstance;
			Price p = price.getData();
			tv = (TextView)view.findViewById(R.id.tvDiscount);
			int discount =  cs.getDiscount(p, doc);
			String text = "";
			if(discount != 0)
				text = Util.IntToScaleStr(discount, Consts.SUM_SCALE);
			tv.setText(text);
		}
	}
}
