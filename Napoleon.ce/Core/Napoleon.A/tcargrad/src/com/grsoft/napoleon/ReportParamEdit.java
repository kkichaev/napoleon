package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.grsoft.dataobjects.OrderPropData;
import com.grsoft.dataobjects.ReportDef;
import com.grsoft.dataobjects.ReportsRequest;
import com.grsoft.dataobjects.impl.ReportDefImpl;
import com.grsoft.dataobjects.impl.ReportsRequestImpl;

public class ReportParamEdit extends PropList {
	private static final String NAME = "name";
	
	ReportsRequestImpl reportRequest = new ReportsRequestImpl();
	
	public static void open(Context context, String name){
		Intent intent = new Intent(context, ReportParamEdit.class);
		intent.putExtra(NAME, name);
		context.startActivity(intent);
	}
	
	@Override
	protected void edited() {
		reportRequest.write();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.report_props);
		
		String id = getIntent().getStringExtra(NAME);
		
		ReportDefImpl rdi = new ReportDefImpl();
		ReportDef rd = rdi.getData();
		rd.id = id;
		rdi.read();
		rdi.close();
		
		TextView tv;
		tv = (TextView)findViewById(R.id.tvName);
		tv.setText(rd.name);
		
		Hashtable<String, com.grsoft.dataobjects.OrderProps> names = new Hashtable<String, com.grsoft.dataobjects.OrderProps>();
		ArrayList<OrderPropData> props = null;
		
		ReportsRequest rr = reportRequest.getData();
		rr.id = id;
		reportRequest.read();
		
		if( rr.items == null || rr.items.size() == 0 )
			props = new ArrayList<OrderPropData>();			
		
		for(com.grsoft.dataobjects.OrderProps op : rd.items) {
			if( props != null ) {
				OrderPropData opd = new OrderPropData();
				opd.id = op.id;
				opd.value = "";

				props.add(opd);
			}
			
			names.put(op.id, op);
		}
	
		if( props != null ) {
			rr.items = props;
			reportRequest.write();
		}
		
		init(rr.items, names);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		reportRequest.close();
	}
}
