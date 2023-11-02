package com.grsoft.napoleon.documents;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.exception.RuntimeException;
import com.grsoft.util.Util;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

public class DebtDocEx extends DebtDoc {
	public static void init() {
		instance = new DebtDocEx();
	}

	@Override
	public DocList docList(String orgId, String order, String where) {
		return new DebtDocListEx(orgId, order, where);
	}
	
	@Override
	public void refreshDocSum() throws RuntimeException {
		Map<String, Long> sums = new HashMap<String, Long>();

		OrgEx oe = new OrgEx();
		String table = DataObjectInfo.getInstance().getTableName(oe.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(oe, table, null);
		while( bdo ) {
			long sum = 0;
			DocList list = docList(oe.id, null);
			for( int i=0; i<list.getCount(); i++ ) {
				Document<?> d = list.get(i);
				sum += d.sum();
			}
			list.close();
			sums.put(oe.id, sum);
			
			bdo = r.selectNext(oe);
		}
		
		r.close();
		writeSumMap(sums);
	}

	@Override
	public void viewClosed(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата");
	}

	@Override
	public void viewOpened(Activity documentsView) {
		TextView tv;
		tv = (TextView)documentsView.findViewById(R.id.DateTitle);
		if( tv != null )
			tv.setText("Дата/Оплата");
	}
	
	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		DataObject dobj = doc.getData();
		if( dobj instanceof Delivery )
			drawDelivery(view, (Delivery)dobj);
		else if( dobj instanceof PaymentEx )
			drawPayment(view, (PaymentEx)dobj);
	}
	
	int getColor(long sum, Date date) {
		int color = Color.BLACK;
		if(sum > 0) {			
			Date blueDate = Util.getDate();
			Date redDate;
			Calendar c = Calendar.getInstance();
			c.setTime(blueDate);
			c.add(Calendar.DATE, -5);
			redDate = c.getTime();
			
			if( date == null || date.compareTo(redDate) <=0 )
				color = Color.RED;
			else if( date != null && date.compareTo(blueDate) <=0 )
				color = Color.BLUE;
		}
		return color;
	}

	private void drawPayment(View view, PaymentEx d) {
		String str;
		int color = getColor(d.sum, (d.dogovor.length() > 0) ? d.payDate : null);
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		if(d.dogovor.length() > 0 ) {
			str = Util.simpleDateFormat.format(d.date);
			str += "\n";
			str += Util.simpleDateFormat.format(d.payDate);
			tv.setText(str);
		}

		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		str = d.number;
		if(d.dogovor.length() > 0 )
			str += "\n" + d.dogovor;
		tv.setText(str);
		tv.setTextColor(color);
	}

	private void drawDelivery(View view, Delivery d) {
		String str;
		int color = getColor(d.sumD, d.payDate);
		TextView tv;
		
		tv = (TextView)view.findViewById(R.id.tvDate);
		tv.setTextColor(color);
		str = Util.simpleDateFormat.format(d.date);
		str += "\n";
		str += Util.simpleDateFormat.format(d.payDate);
		tv.setText(str);
		
		tv = (TextView)view.findViewById(R.id.tvSum);
		tv.setTextColor(color);
					
		tv = (TextView)view.findViewById(R.id.tvOther);
		tv.setTextColor(color);
	}
}

class DebtDocListEx extends DebtDocList {
	public DebtDocListEx(String orgId, String order, String where) {
		String whereStr = "";
		String wherePay = "";
		if( orgId != null ) {
			whereStr = "id='" + orgId + "'";

			OrgImpl oi = new OrgImpl();
			OrgEx o = (OrgEx)oi.getData();
			o.id = orgId;
			wherePay = whereStr;
			if( oi.read() )
				wherePay = "((" + whereStr + " and dogovor = '') or ( ido='" + o.ido + "' and dogovor <>''))";
			else
				wherePay = whereStr;
			oi.close();
		}
		if( where != null && where.length() > 0 ) {
			whereStr += " AND " + where;
			wherePay += " AND " + where;
		}
		deliveries = new DocList(BalanceDelivery.class, whereStr, order);
		payments = new DocList(PaymentImpl.class, wherePay, order);
		
		orderDocuments();
	}
}
