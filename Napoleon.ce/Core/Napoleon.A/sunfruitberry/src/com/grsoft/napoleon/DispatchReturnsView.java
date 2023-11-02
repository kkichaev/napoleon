package com.grsoft.napoleon;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.grsoft.dataobjects.DispatchReturnsItem;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.DispatchReturnsInfoImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class DispatchReturnsView extends BaseActivity implements OnClickListener {
	
	protected PriceImpl priceImpl = new PriceImpl();
	private ListView lvItems;
	LinesCountController linesController;
	DispatchReturnsInfoImpl doc;
	View btnRemark;
	
	Map<String, String> retCause = new HashMap<String, String>(); 
	
	public static void open(Context context, DispatchReturnsInfoImpl doc) {
		Intent i = new Intent(context, DispatchReturnsView.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dr_view);
		doc = new DispatchReturnsInfoImpl();
		
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		OrgImpl orgImpl = new OrgImpl();
		orgImpl.getData().id = doc.getData().id;
		orgImpl.read();	
		orgImpl.close();
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(orgImpl.getData().name);

		ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
		
		lvItems = (ListView) findViewById(R.id.lvItems);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lvItems, btnLines, this, true);
		linesController = linesOnClickListener.getController();
		
		lvItems.setAdapter(new ItemsAdapter());
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl ci = new ConfigImpl();
		ci.getValue(sb, "ПричиныВозвратовДоставка");
		String[] values = sb.toString().split(";");
		for(String v : values) {
			String[] kv = v.split("\t");
			if(kv.length == 2)
				retCause.put(kv[1], kv[0]);
		}
		
		View btnCreate = findViewById(R.id.btnCreateOrder);
		btnCreate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				OrderImpl crDoc;
				if(doc.getData().createdOrder != 0 ) {
					crDoc = (OrderImpl) OrderDoc.instance().create();
					crDoc.getData().created = new Date(doc.getData().createdOrder);
					crDoc.read();
					crDoc.close();
				} else {
					crDoc = doc.createOrder();
				}
				if(crDoc != null) {
					crDoc.open(DispatchReturnsView.this);
					DocType.setCurDoc(OrderDoc.instance());
					finish();
				}
			}
		});
		
		btnRemark = findViewById(R.id.btnRemark);
		
		if (doc.getData().remark.length() == 0)
			btnRemark.setVisibility(View.GONE);
		else {
			btnRemark.setVisibility(View.VISIBLE);
			btnRemark.setOnClickListener(this);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		doc.markReaded();
		doc.close();
		priceImpl.close();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	class ItemsAdapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }
		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if(view == null) {
				view = View.inflate(DispatchReturnsView.this, R.layout.dr_view_row, null);
			}
			DispatchReturnsItem item = (DispatchReturnsItem) getItem(arg0);
			TextView tvName = (TextView)view.findViewById(R.id.tvName);
			TextView tvQty = (TextView)view.findViewById(R.id.tvQty);
			TextView tvCause = (TextView)view.findViewById(R.id.tvCause);
			
			linesController.prepareTextView(tvName);
			linesController.prepareTextView(tvName);

			Price p = priceImpl.getData();
			p.id = item.id;
			String text;
			if( priceImpl.read() )
				text = p.name;
			else
				text = "< '" + getString(R.string.id) + " " + item.id + "' >";
			
			tvName.setText(text);			
			
			text = retCause.get(item.cause);
			if(text == null)
				text = item.cause;
			tvCause.setText(text);
			tvQty.setText(Util.IntToScaleStr(item.qty, Consts.QTY_SCALE));
			return view;
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnRemark) 
			DispatchReturnRemark.open(this, doc.getRowid());
	}
}
