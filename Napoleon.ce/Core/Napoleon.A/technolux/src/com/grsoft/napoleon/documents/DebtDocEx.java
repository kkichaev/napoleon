package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Delivery;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.PaymentEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PaymentImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx(DOC_NAME, Debt.class);
	}
	
	protected DebtDocEx(String name, Class<? extends Document<?>> docClass) {
		super(name, docClass);
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

		Delivery d = null;
		DataObject dobj = doc.getData();
		if( dobj instanceof Delivery )
			d = (Delivery)dobj;

		if( d == null )
			return;

		String str;
		int color = (d.sumD > 0 && d.payDate.compareTo(Util.getDate()) < 0) ? Color.RED : Color.BLACK;
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
	
	@Override
	protected String getOrgWhere(String orgId) {
		if( orgId != null && orgId.length() > 0 ) {
			OrgImpl oi = new OrgImpl();
			OrgEx oe = (OrgEx)oi.getData();
			oe.id = orgId;
			oi.read();
			oi.close();
			return "ido='" + oe.ido + "'";
		}
		return super.getOrgWhere(orgId);
	}
	
	@Override
	public void refreshDocSum() throws com.grsoft.network.exception.RuntimeException {
		DbWriter.checkDBTable(OrgSum.class);
		Map<String, Long> sums = new HashMap<String, Long>();
		DocList list = docList(null, null);
		for( int i=0; i<list.getCount(); i++ ) {
			Document<?> d = list.get(i);
			String id = "";
			if( d instanceof PaymentImpl)
				id = ((PaymentEx)d.getData()).ido;
			else if(d instanceof DeliveryImpl)
				id = ((DeliveryEx)d.getData()).ido;
			if( id.length() == 0 )
				continue;
			long sum = d.sum();
			if( sums.containsKey(id))
				sum += sums.get(id);
			
			sums.put(id, sum);
		}
		list.close();
		
		SQLiteDatabase db = DataBaseManager.getDataBase();
		String tableName = DataObjectInfo.getInstance().getTableName(OrgSum.class);
		db.execSQL(String.format("DELETE FROM '%s' WHERE type='%s'", tableName, name));
		SQLiteStatement stmt = db.compileStatement("insert or replace into " + tableName + "(\"sum\", \"type\", \"id\") select ?, '" + name + 
				"', id from org where ido=?" );
		
		for( Entry<String, Long> v : sums.entrySet() ) {
			stmt.clearBindings();
			stmt.bindLong(1, v.getValue());
			stmt.bindString(2, v.getKey());
			stmt.execute();
		}
		
		stmt.close();
	}
}
