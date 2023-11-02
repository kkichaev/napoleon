package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.MerchImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.dataobjects.impl.PriceImpl;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.MerchDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.script.ScriptActivity;
import com.grsoft.script.ScriptHelper;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MerchDetail extends BaseActivity implements OnClickListener, OnChildClickListener,
		DataSetNotify, SendResultListener, ScriptActivity {
	private MerchImpl doc = new MerchImpl();
	private MerchDetailAdapter adapter;
	private long docRowID = ExtrasConst.INVALID_ROWID;
	private ExpandableListView list;
	private TextView tvOrg;
	private ImageButton btnLines;
	protected ImageButton btnAddItems;
	protected ImageButton btnSend;
	protected static final int CANT_SEND_EMPTY_DOC_DLG = R.id.cant_send_empty_doc_dlg;
	
	public static void open(Context context, long rowid) {
		Intent i = new Intent(context, MerchDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.merchdetail);
		list = (ExpandableListView) findViewById(R.id.list);
		tvOrg = (TextView) findViewById(R.id.tvOrg);
		btnLines = (ImageButton) findViewById(R.id.btnLines);
		btnAddItems = (ImageButton) findViewById(R.id.btnAddItems);
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		
		docRowID = getIntent().getLongExtra(ExtrasConst.DOC_ROW_ID_STR, ExtrasConst.INVALID_ROWID);
		doc.read(docRowID);
		
		OrgImpl org = new OrgImpl();
		org.read("id", doc.getId());
		
		tvOrg.setText(org.getData().name);
		
		LinesOnClickListener linesOnClickListener = new LinesOnClickListener(list, btnLines, this, true);
		adapter = new MerchDetailAdapter(this);
		adapter.setLinesController(linesOnClickListener.getController());
		list.setAdapter(adapter);
		list.setOnChildClickListener(this);
		registerForContextMenu(list);

		btnAddItems.setOnClickListener(this);
		
		btnSend = (ImageButton) findViewById(R.id.btnSend);
		ScriptHelper.initView(this, MerchDoc.instance().getObjectName(), doc.getData().created, doc.getId() );

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
	
	public void send() {
		new DocumentSender(this, btnSend, 
				MerchDoc.getCurDoc().getObjectName(), doc, 
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
		
		adapter.refresh(this, doc);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnAddItems)
			Warehouse.open(this, doc, true);
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		MerchDetailAdapter.Item i = (MerchDetailAdapter.Item)parent.getExpandableListAdapter().getChild(groupPosition, childPosition);
		
		if (i instanceof MerchDetailAdapter.FolderItem) {
			doc.editFolder(this, i.id, R.id.tvMine);
		}else if (i instanceof MerchDetailAdapter.PriceItem) {
			PriceImpl price = new PriceImpl();
			price.read("id", i.id);
			doc.editItem(price.getRowid(), this);
		}

		return true;
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
	protected void onPause() {
		super.onPause();
		
		if (isFinishing() && doc.isEditable() && doc.isEmpty()) {
			doc.delete();
			doc.close();
		}
	}

	@Override
	public boolean closeDocument() {
		return true;
	}
}
