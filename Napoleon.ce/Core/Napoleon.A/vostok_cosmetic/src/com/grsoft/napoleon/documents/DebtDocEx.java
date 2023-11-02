package com.grsoft.napoleon.documents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.BalanceDeliveryEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;


public class DebtDocEx extends DebtDoc {

	Date minDate;
	protected DebtDocEx() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, -5);
		minDate = c.getTime();
	}
	
	public static void initialize() { instance = new DebtDocEx(); }
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null ) 
			tv.setText("Дата отгр/Оплаты");
	}
	
	@Override
	public void viewClosed(Activity documentsView) {
		super.viewClosed(documentsView);
		TextView tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null ) 
			tv.setText("Дата");
	}
	
	@Override
	public DocList docList(String orgId, String order, String where) {
		
		String dlvStr = "";		
		if( orgId != null ) {
			OrgImpl oi = new OrgImpl();
			oi.getData().id = orgId;
			oi.read();
			oi.close();
			
			dlvStr = "ido='" + ((OrgEx)oi.getData()).ido + "'";
		}
		
		if( where != null && where.length() > 0 )
			dlvStr += " AND " + where;
		
		String payStr = (orgId == null) ? "" : "id='" + orgId + "'";
		if( where != null && where.length() > 0 )
			payStr += " AND " + where;
		return new DebtDocListEx(dlvStr, payStr, order);
	}
	
	class DebtDocListEx extends DebtDocList {
		@Override protected Class<? extends Document<?>> getDeliveryType() { return BalanceDeliveryEx.class; }

		public DebtDocListEx(String dlvWhere, String payWhere, String order) {
			ids = new ArrayList<Long>();

			deliveries = new DocList(BalanceDeliveryEx.class, dlvWhere, order);
			payments = new DocList(PaymentImpl.class, payWhere, order);
			
			HashSet<String> dlvNumbers = new HashSet<String>();
			HashSet<String> payNumbers = new HashSet<String>();
			loadDeliveries(dlvNumbers);
			loadPayments(payNumbers);
			
			orderDocuments();
		}
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		DeliveryEx d = null;
		if( doc.getData() instanceof DeliveryEx )
			d = (DeliveryEx)doc.getData();
		
		if( d == null ) {
			super.setView(adapter, view, doc);
			return;
		}

		Date check = new Date();
		int color = Color.BLACK;
		if( d.payDate.before(check) && d.payDate.after(minDate) && d.sumD > 0 )
			color = Color.RED;
		
		TextView tv = (TextView)view.findViewById(R.id.tvDate);
		String text = Util.simpleDateFormat.format(d.date) + "\n" + Util.simpleDateFormat.format(d.payDate);
		tv.setText(text);
		tv.setTextColor(color);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setVisibility(View.VISIBLE);
		tv.setText(Util.IntToScaleWStr(doc.sum(), Consts.SUM_SCALE, 2, false));
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setText(doc.getDescription(view.getContext()));		
		tv.setTextColor(color);
	}
}
