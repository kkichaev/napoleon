package com.grsoft.napoleon;

import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.dataobjects.impl.MonitoringImpl;
import com.grsoft.dataobjects.impl.MonitoringImplBase;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MonitoringDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.script.dataobjects.impl.ScriptImpl;
import com.grsoft.util.DataSetNotify;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.LinesOnClickListener;
import com.grsoft.util.OnClickListenerToNotify;
import com.grsoft.view.BaseActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MonitoringDetail extends BaseActivity implements OnClickListener, DataSetNotify, SendResultListener, OnItemClickListener  {
	protected MonitoringImplBase<?> doc;
	private MonitoringDetailAdapter adapter;
	private long docRowID = ExtrasConst.INVALID_ROWID;
	private ListView list;
	private TextView tvOrg;
	private ImageButton btnLines;
	protected ImageButton btnAddItems;
	protected ImageButton btnSend;
	protected static final int CANT_SEND_EMPTY_DOC_DLG = R.id.cant_send_empty_doc_dlg;
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, MonitoringDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	protected MonitoringImplBase<?> createDoc() { return new MonitoringImpl(); }
	
	protected MonitoringDetailAdapter createAdapter(Context ctx) { return new MonitoringDetailAdapter(ctx); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.monitoringdetail);
		list = (ListView) findViewById(R.id.list);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		btnAddItems = (ImageButton) findViewById(R.id.btnAddItems);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		
		doc = createDoc();
		docRowID = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(docRowID);
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		
		tvOrg.setText(org.getData().name);
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(list, btnLines, this, true);
		adapter = createAdapter(this);
		adapter.setLinesController(linesOnClickListener.getController());
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		registerForContextMenu(list);

		btnAddItems.setOnClickListener(this);
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		if( Features.CANT_SEND_SCRIPT_PART ) {
			if(ScriptImpl.containsDocument(MonitoringDoc.instance().getObjectName(), doc.getData().created, doc.getId()))
				btnSend.setVisibility(View.GONE);
		}
		
		btnSend.setOnClickListener(new OnClickListenerToNotify() {			
			@Override
			public void onClick(View v) {
				super.onClick(v);
				
				if(!Features.CAN_SEND_EMPTY_DOCS && doc.isEmpty() )
					showDialog(CANT_SEND_EMPTY_DOC_DLG);
				else
					send();
			}
		});
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
		MonitoringItem mi = (MonitoringItem)adapter.getItem(((AdapterContextMenuInfo)
				item.getMenuInfo()).position);
		
		if (item.getItemId() == R.id.itDelete) {
			deleteItem(mi);
		} else if (item.getItemId() == R.id.itEdit) {
			editItem(mi);
		}
		
		return super.onContextItemSelected(item);
	}
	
	protected void editItem(MonitoringItem item) {
		PriceImpl pi = new PriceImpl();
		pi.getData().id = item.id;
		pi.read();
		pi.close();
		doc.editItem(pi.getRowid(), this);
	}

	private void deleteItem(MonitoringItem item) {
		doc.deleteItem(item.id);
		reloadList();
	}

	public void send() {
		new DocumentSender(this, btnSend, 
				MonitoringDoc.getCurDoc().getObjectName(), doc, 
					doc.getRowid(), this).execute((Void[])null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		reloadList();
	}

	private void reloadList() {
		doc.read(docRowID, false);
		doc.close();
		
		adapter.refresh(doc);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnAddItems)
			Warehouse.open(this, doc, true);
	}

	@Override
	public void notifyDataSetChanged() {
		reloadList();
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == CANT_SEND_EMPTY_DOC_DLG)
			return cantSendEmptyDocDlg();
		else
			return super.onCreateDialog(id);
	}
	
	private Dialog cantSendEmptyDocDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.error);
		builder.setMessage(R.string.cant_send_empty_doc_str);
		return builder.create();
	}

	@Override
	public void postSendExecute(boolean result) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MonitoringItem i = (MonitoringItem) parent.getItemAtPosition(position);
		
		PriceImpl price = new PriceImpl();
		price.read("id", i.id);
		doc.editItem(price.getRowid(), this);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if (isFinishing() && doc.isEditable() && doc.isEmpty()) {
			doc.delete();
			doc.close();
		}
	}
}
