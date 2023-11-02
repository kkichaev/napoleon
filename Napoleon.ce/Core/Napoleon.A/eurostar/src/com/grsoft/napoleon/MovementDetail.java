package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.grsoft.dataobjects.MovementItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Price;
import com.grsoft.dataobjects.impl.MovementWhImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.util.Util;
import com.grsoft.view.BaseActivity;

public class MovementDetail extends BaseActivity implements SendResultListener, DataSetNotify {
	MovementWhImpl doc;
	ImageButton btnSend;
	Adapter adapter;
	LinesCountController linesController;
	PriceImpl pi = new PriceImpl();
	
	public static void open(Context context, MovementWhImpl doc) {
		Intent i = new Intent(context, MovementDetail.class);
		
		if( doc != null )
			i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.movement_detail);

		doc = (MovementWhImpl) MovementDoc.instance().create();
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		long rowid = b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(rowid);
		
		OrgImpl org = new OrgImpl();
		Org o = org.getData();
		o.id = doc.getId();
		org.read();
		org.close();
		
		
		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(o.name);
		
		findViewById(R.id.btnEditOrder).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { MovementProps.open(v.getContext(), doc, false); }
		});

		findViewById(R.id.btnAddItems).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { WhMovement.open(v.getContext(), doc, true); }
		});
	
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(DocType.getCurDoc().getObjectName(), doc.getData().created, doc.getId()))
				btnSend.setVisibility(View.GONE);
		}
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				
				if(!Features.CAN_SEND_EMPTY_DOCS && doc.getData().items.size() == 0 ) {
					Toast.makeText(v.getContext(), R.string.cant_send_empty_doc_str, Toast.LENGTH_SHORT).show();
				} else
					send();
			}
		});
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		registerForContextMenu(lv);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) { editItem(arg2); }
		
		});
	
		ImageButton btnLines = (ImageButton) findViewById(R.id.btnLines);
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lv, btnLines, this, true);
		linesController = linesOnClickListener.getController();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)	{
		if (doc.isEditable())
			getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	public void onBackPressed() {
		if( doc.isEditable() && doc.getData().items.size() == 0 )
			doc.delete();
		finish();
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int pos = ((AdapterContextMenuInfo)item.getMenuInfo()).position;
		
		if (item.getItemId() == R.id.itDelete) {
			deleteItem(pos);
		} else if (item.getItemId() == R.id.itEdit) {
			editItem(pos);
		}
		
		return super.onContextItemSelected(item);
	}

	protected void deleteItem(int pos) {
		MovementItem mi = doc.getData().items.get(pos);
		
		Price p = pi.getData();
		p.id = mi.id;
		pi.read();

		doc.updateQty(pi, 0, 0, false);
		adapter.notifyDataSetChanged();
	}

	protected void editItem(int pos) {
		MovementItem mi = doc.getData().items.get(pos);
		Price p = pi.getData();
		p.id = mi.id;
		pi.read();

		doc.editItem(pi.getRowid(), this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		doc.close();
		pi.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		doc.read(doc.getRowid(), false);
		adapter.notifyDataSetChanged();
	}
	
	void send() {
		new DocumentSender(this, btnSend, MovementDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}

	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}
	
	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items.size(); }
		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(MovementDetail.this, R.layout.movement_list_row, null);
			
			MovementItem mi = (MovementItem) getItem(arg0);
			
			Price p = pi.getData();
			p.id = mi.id;
			pi.read();
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			linesController.prepareTextView(tv);
			tv.setText(p.name);
			
			tv = (TextView)view.findViewById(R.id.tvQty);
			tv.setText(Util.IntToScaleStr(mi.qty, Consts.QTY_SCALE));
			
			return view;
		}
	}

	@Override public void notifyDataSetChanged() { adapter.notifyDataSetChanged(); }
}
