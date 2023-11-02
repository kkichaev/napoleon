package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Hashtable;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.OrderPropData;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.ExtrasConst;

public class OrderProps extends PropList {
	OrderImplBase<? extends Order> doc;
	
	public static void open(Context ctx, OrderImplBase<? extends Order> doc) {
		Intent i = new Intent(ctx, OrderProps.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.order_props);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState; 
		long rid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ID);
		doc = (OrderImplBase<? extends Order>) OrderDoc.instance().create();
		doc.read(rid);
		
		initProps();
	}
	
	@Override
	protected void edited() {
		doc.write();
	}

	protected void initProps() {
		ArrayList<OrderPropData> props = null;
		OrderEx o = (OrderEx)doc.getData();
		if( o.props == null || o.props.size() == 0 )
			props = new ArrayList<OrderPropData>();			
		
		Hashtable<String, com.grsoft.dataobjects.OrderProps> names = new Hashtable<String, com.grsoft.dataobjects.OrderProps>();
		
		com.grsoft.dataobjects.OrderProps op = new com.grsoft.dataobjects.OrderProps();
		String table = DataObjectInfo.getInstance().getTableName(com.grsoft.dataobjects.OrderProps.class);
		DbReader r = new DbReader();
		boolean bdo = r.select(op, table, null, "name");
		while( bdo ) {				
			if( props != null ) {
				OrderPropData opd = new OrderPropData();
				opd.id = op.id;
				opd.value = "";

				props.add(opd);
			}
			
			names.put(op.id, op);
			op = new com.grsoft.dataobjects.OrderProps();
			
			bdo = r.selectNext(op);
		}
		r.close();
		
		if( props != null ) {
			o.props = props;
			doc.write();
		}
		
		init(o.props, names);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}	
}

