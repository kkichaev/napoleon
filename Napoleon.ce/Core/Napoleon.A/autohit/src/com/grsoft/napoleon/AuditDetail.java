package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.grsoft.dataobjects.Goods;
import com.grsoft.dataobjects.GoodsAuditItem;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.GoodsAuditImpl;
import com.grsoft.dataobjects.impl.GoodsImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.documents.ActiveOrgActionsDoc;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.GoodsAuditDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.util.LinesCountController;
import com.grsoft.util.Consts;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.Util;
import com.grsoft.view.RegDurationActivity;

public class AuditDetail extends RegDurationActivity implements DataSetNotify, SendResultListener {
	GoodsAuditImpl doc = new GoodsAuditImpl();
	GoodsImpl goods = new GoodsImpl();
	Adapter adapter;
	boolean started = true;
	LinesCountController linesController;

	public static void open(Context context, GoodsAuditImpl doc) {
		Intent i = new Intent(context, AuditDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.audit_detail);
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		doc.read(b.getLong(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID));
		
		OrgImpl o = new OrgImpl();
		Org org = o.getData();
		org.id = doc.getId();
		o.read();
		o.close();

		TextView tvOrg = (TextView) findViewById(R.id.tvOrg);
		tvOrg.setText(org.name);

		findViewById(R.id.btnAddItems).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { AuditGoods.open(v.getContext(), doc, true); }
		});
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { send(); }
		});
		
		adapter = new Adapter();
		ListView lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) { 
				GoodsAuditItem item = (GoodsAuditItem)arg0.getAdapter().getItem(arg2);
				editItem(item);
			}
		});
		registerForContextMenu(lv);

		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(lv, (ImageButton)findViewById(R.id.btnLines), this, true);
		linesController = linesOnClickListener.getController();
	}
		
	@Override
	public void onBackPressed() {
		if(doc.isEditable() && doc.getData().items.size() == 0) {
			String id = doc.getId();
			doc.delete();
			ActiveOrgActionsDoc.instance().refreshDocSum(id);
		}
		super.onBackPressed();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.order_detail_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if( doc.isEditable() ) {
			GoodsAuditItem orderItem = (GoodsAuditItem)((AdapterContextMenuInfo)item.getMenuInfo()).targetView.getTag();
			
			if (item.getItemId() == R.id.itDelete) {
				doc.deleteItem(orderItem);
				notifyDataSetChanged();
			} else if (item.getItemId() == R.id.itEdit) {
				editItem(orderItem);
			}
		}
		return super.onContextItemSelected(item);
	}

	private void editItem(GoodsAuditItem orderItem) {
		GoodsImpl pi = new GoodsImpl();
		pi.getData().id = orderItem.id;
		pi.read();
		pi.close();
		
		doc.editItem(pi.getRowid(), this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if( started )
			started = false;
		else {
			doc.read(doc.getRowid(), false);
			notifyDataSetChanged();
		}
	}
	
	@Override
	protected void onDestroy() {
		doc.close();
		goods.close();
		super.onDestroy();
	}
	
	protected void send() {
		new DocumentSender(this, findViewById(R.id.btnSend), GoodsAuditDoc.instance().getObjectName(), doc, doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
	}
	
	@Override
	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void postSendExecute(boolean result) {
		if( result )
			doc.read(doc.getRowid(), false);
	}

	class Adapter extends BaseAdapter {

		@Override public int getCount() { return doc.getData().items == null ? 0 : doc.getData().items.size(); }
		@Override public Object getItem(int arg0) { return doc.getData().items.get(arg0); }
		@Override public long getItemId(int arg0) { return arg0; }

		@Override
		public View getView(int arg0, View view, ViewGroup arg2) {
			if( view == null )
				view = View.inflate(AuditDetail.this, R.layout.audit_detail_row, null);
			GoodsAuditItem item = (GoodsAuditItem)getItem(arg0);
			Goods g = goods.getData();
			g.id = item.id;
			goods.read();
			
			view.setTag(item);
			
			TextView tv;
			tv = (TextView)view.findViewById(R.id.tvName);
			linesController.prepareTextView(tv);
			tv.setText(g.name);

			String text;
			text = String.format("%s<br>%s", 
					Util.IntToScaleStr(item.shelfAll, Consts.SUM_SCALE, Util.DEC_DELIM, false),
					Util.IntToScaleStr(item.shelfOur, Consts.SUM_SCALE, Util.DEC_DELIM, false));

			tv = (TextView)view.findViewById(R.id.tvShelf);
			tv.setText(Html.fromHtml(text));

			text = String.format("%s<br>%s", 
					Util.IntToScaleStr(item.scuAll, Consts.SUM_SCALE, Util.DEC_DELIM, true),
					Util.IntToScaleStr(item.scuOur, Consts.SUM_SCALE, Util.DEC_DELIM, true));

			tv = (TextView)view.findViewById(R.id.tvScu);
			tv.setText(Html.fromHtml(text));
			return view;
		}
		
	}
}
