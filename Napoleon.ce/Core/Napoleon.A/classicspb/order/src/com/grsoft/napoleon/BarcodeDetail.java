package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.BarcodeItem;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.BarcodeImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.BarcodeDoc;
import com.grsoft.napoleon.documents.DocSendListner;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.network.DocExportListener;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.view.BaseActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BarcodeDetail extends BaseActivity implements OnClickListener, SendResultListener {
	BarcodeImpl doc = new BarcodeImpl();
	TextView tvOrg;
	ListView list;
	PriceImpl price = new PriceImpl();
	View btnAdd;
	View btnLines;
	View btnSend;
	protected LinesCountController linesController;
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, BarcodeDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.barcodedetail);
		
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		list = (ListView) findViewById(R.id.list);
		btnAdd = findViewById(R.id.btnAdd);
		btnSend = findViewById(R.id.btnSend);
		btnLines = findViewById(R.id.btnLines);
		
		btnAdd.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		
		
		doc.read(getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		doc.close();
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		
		tvOrg.setText(org.getData().name);
		list.setAdapter(new ItemsAdapter());
		list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BarcodeItem i = (BarcodeItem) parent.getItemAtPosition(position);
				editItem(i);
			}
		});
		
		registerForContextMenu(list);
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(list, (ImageView)btnLines, this, true);
		linesController = linesOnClickListener.getController();
	}
	
	public void editItem(BarcodeItem i) {
		price.getData().id = i.id;
		price.read();
		price.close();
		doc.editItem(price.getRowid(), BarcodeDetail.this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)	{
		if (doc.isEditable())
			getMenuInflater().inflate(
				R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		int pos = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		BarcodeItem bi = (BarcodeItem)((ItemsAdapter)list.getAdapter()).getItem(pos);
		
		if (item.getItemId() == R.id.itDelete) {
			deleteItem(bi);
		} else if (item.getItemId() == R.id.itEdit) {
			editItem(bi);
		}
		
		return super.onContextItemSelected(item);
	}
	
	public void deleteItem(BarcodeItem i) {
		doc.deleteItem(i.id);
		((BaseAdapter)list.getAdapter()).notifyDataSetChanged();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing() && 
				doc.isEditable() && 
				doc.getData().items.size() == 0) {
			
			doc.delete();
			doc.close();
		}
	}
	
	public class ItemsAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return doc.getData().items.size();
		}

		@Override
		public Object getItem(int position) {
			return doc.getData().items.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null)
				convertView = View.inflate(getApplicationContext(), R.layout.barcodedetailrow, null);
			
			BarcodeItem i = (BarcodeItem) getItem(position);
			price.read("id", i.id);
			
			TextView tv = (TextView) convertView.findViewById(R.id.tvName);
			tv.setText(price.getData().name);
			linesController.prepareTextView(tv);
			
			return convertView;
		}
		
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnSend)
			send();
		else if (v.getId() == R.id.btnAdd)
			add();
	}

	private void add() {
		Warehouse.open(this, doc, true);
	}

	private void send() {
		if(!doc.isEmpty()) {
			List<DocExportListener> sendDocs = new ArrayList<DocExportListener>();
			sendDocs.add(new DocSendListner(BarcodeDoc.instance().getObjectName(), doc));
			
			VisitImpl v = new VisitImpl();
			v.getData().created = doc.getData().visitDoc;
			v.read();
			v.close();
			
			sendDocs.add(new DocSendListner(VisitDoc.instance().getObjectName(), v));
				
			new DocumentSender(BarcodeDetail.this, findViewById(R.id.btnSend), sendDocs, BarcodeDetail.this).execute((Void[]) null);
		};
	}

	@Override
	public void postSendExecute(boolean result) {
		// TODO Auto-generated method stub
	}
	
	
}
