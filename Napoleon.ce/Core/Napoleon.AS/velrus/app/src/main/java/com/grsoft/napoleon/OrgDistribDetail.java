package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import com.grsoft.dataobjects.DistribMatrix;
import com.grsoft.dataobjects.DistribMatrixItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.DistribMatrixImpl;
import com.grsoft.dataobjects.impl.OrgDistribImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.OrgDistribDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.view.RegDurationActivity;

public class OrgDistribDetail extends RegDurationActivity implements SendResultListener {

	boolean started = true;
	Adapter adapter;
	OrgDistribImpl doc = new OrgDistribImpl();
	PriceImpl price = new PriceImpl();
	List<Price> activeActions;
	private ImageButton btnLines;
	private LinesCountController linesController;
	
	public static void open(Context context, OrgDistribImpl doc) {
		Intent i = new Intent(context, OrgDistribDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.org_distrib_detail);
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		
		OrgImpl o = new OrgImpl();
		Org org = o.getData();
		org.id = doc.getId();
		o.read();
		o.close();

		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.name);

		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
		
		adapter = new Adapter();
		adapter.refresh();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setDividerHeight(0);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				DataItem item = (DataItem) arg0.getAdapter().getItem(arg2);
				doc.changeItem(item.id);
				adapter.refresh();
				adapter.notifyDataSetChanged();
			}
		});
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(
				lv, btnLines, OrgDistribDetail.this, true);
		linesController = linesOnClickListener.getController();
	}
	
	View.OnClickListener changeAction = new View.OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			String oa = (String)arg0.getTag();
			doc.changeItem(oa);
			adapter.refresh();
			adapter.notifyDataSetChanged();
		}
	};
	
	protected void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), OrgDistribDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
	}
	
	@Override
	protected void onDestroy() {
		doc.close();
		price.close();
		super.onDestroy();
	}

	@Override
	public void postSendExecute(boolean result) {
		doc.read(doc.getRowid(), false);
	}
	
	@Override
	public void onBackPressed() {
		if(doc.isEditable() && doc.getData().items.size() == 0) {
			String id = doc.getId();
			doc.delete();
			OrgDistribDoc.instance().refreshDocSum(id);
		}
		super.onBackPressed();
	}

	class Adapter extends BaseAdapter {
		List<DataItem> items;
		
		public Adapter() { refresh(); }
		
		@Override public int getCount() { return items.size(); }
		
		public void refresh() {
			items = new ArrayList<DataItem>();
			
			HashMap<String, DistribMatrixItem> docItems = new HashMap<String, DistribMatrixItem>();
			for(DistribMatrixItem ai : doc.getData().items) 
				docItems.put(ai.id, ai);

			DistribMatrixImpl di = new DistribMatrixImpl();
			DistribMatrix dm = di.getData();
			dm.id = doc.getId();
			
			if(!di.read()){
				dm.id = "";
				di.read();
			}
				
			di.close();
			
			Price oa = price.getData();
			for( DistribMatrixItem item : dm.items) {
				oa.id = item.id;
				if( price.read() ) {
					DataItem ditem = new DataItem(oa, docItems.get(oa.id));
					items.add(ditem);
				}
			}
			
			Collections.sort(items);
		}

		@Override public Object getItem(int arg0) { return items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int pos, View view, ViewGroup arg2) {
			if( view == null ) {
				view = View.inflate(OrgDistribDetail.this, R.layout.org_distrib_detail_row, null);
			}
			
			DataItem item = (DataItem)getItem(pos);

			TextView tv = (TextView)view.findViewById(R.id.tvName);
			linesController.prepareTextView(tv);
			tv.setText(item.name);
			
			CheckBox cb = (CheckBox)view.findViewById(R.id.cbAction);
			cb.setChecked(item.item != null);
			cb.setTag(item.id);
			cb.setOnClickListener(changeAction);
			
			view.setBackgroundResource(pos % 2 != 0 ? 
					R.drawable.even_row_selector :
					R.drawable.list_selector);		
			
			return view;
		}		
	}
}

class DataItem implements Comparable<DataItem> {
	public DataItem(Price oa, DistribMatrixItem item) {
		this.id = oa.id;
		this.name = oa.name;
		this.item = item;
	}
	
	public String id;
	public String name;
	public DistribMatrixItem item;
	
	@Override
	public int compareTo(DataItem arg0) {
		return name.compareTo(arg0.name);
	}
}
