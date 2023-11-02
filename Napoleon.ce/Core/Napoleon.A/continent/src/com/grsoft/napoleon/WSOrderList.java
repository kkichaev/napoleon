package com.grsoft.napoleon;

import com.grsoft.dataobjects.Requestdoc;
import com.grsoft.dataobjects.WSOrder;
import com.grsoft.dataobjects.impl.ApplyWSOrderImpl;
import com.grsoft.dataobjects.impl.RequestdocImpl;
import com.grsoft.dataobjects.impl.WSOrderImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeSender;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.WSOrderDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.view.Refreshable;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class WSOrderList extends Activity implements Refreshable, OnClickListener, SendResultListener {
	private ImageButton btnNewDoc;
	private ListView list;
	
	public static void open(Context context){
		Intent intent = new Intent(context, WSOrderList.class);
		context.startActivity(intent);
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wsorderlist);
		
		DocType.setCurDoc(WSOrderDoc.instance());
		
		btnNewDoc = (ImageButton) findViewById(R.id.btnNewDoc);
		list = (ListView) findViewById(R.id.lvDocs);
		
		btnNewDoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				WSOrderImpl order = new WSOrderImpl();
				
				if(order.init(v.getContext(), null,GpsCoord.empty))
					order.open(v.getContext());
			}
		});
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				WSOrderImpl o = (WSOrderImpl) parent.getAdapter().getItem(position);
				o.open(view.getContext());
			}
		});
		registerForContextMenu(list);
		findViewById(R.id.btnSend).setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		list.setAdapter(new DocumentsAdapter(this, WSOrderDoc.instance(), "", "created desc"));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			Adapter a = list.getAdapter();
			
			if(a != null && a instanceof DocumentsAdapter)
				((DocumentsAdapter)a).close();
		
			DocType.setCurDoc(OrderDoc.instance());
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate( R.menu.wsorderlist_context_menu, menu);
		menu.removeItem(R.id.itCopy);
		
		Adapter adapter = list.getAdapter();
		CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(((AdapterContextMenuInfo)menuInfo).position);
		if( doc != null ) {
			if (((WSOrder)doc.getData()).number.length() == 0) {
				menu.removeItem(R.id.itApply);
				menu.removeItem(R.id.itRequestMoving);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();	
		Adapter adapter = list.getAdapter();
		CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
		if( doc != null ) {
			if (item.getItemId() == R.id.itDelete)
				docDelete(doc);
			else if (item.getItemId() == R.id.itEdit)
				doc.open(this);
			else if (item.getItemId() == R.id.itApply) {
				applay(doc);
				Toast.makeText(this, "Принято", Toast.LENGTH_SHORT).show();
			}
			else if (item.getItemId() == R.id.itRequestMoving) {
				requestMoving(doc);
				Toast.makeText(this, "Документ перемещение заказан", Toast.LENGTH_SHORT).show();
			}
		}
		return true;
	}
	
	private void requestMoving(CreatableDocument<?> doc) {
		WSOrderImpl ord = (WSOrderImpl) doc;
		RequestdocImpl request = new RequestdocImpl();
		request.init(this, "", GpsCoord.empty);
		request.getData().number = ord.getData().number;
		request.getData().type = Requestdoc.MOVING_TYPE;
		request.write();
		request.close();
	}

	private void applay(CreatableDocument<?> doc) {
		WSOrderImpl ord = (WSOrderImpl) doc;
		ApplyWSOrderImpl request = new ApplyWSOrderImpl();
		request.init(this, "", GpsCoord.empty);
		request.getData().number = ord.getData().number;
		request.write();
		request.close();
	}

	protected void docDelete(CreatableDocument<?> doc) {
		DocDeleteHelper.delete(doc, this);
	}

	@Override
	public void refreshContent() {
		((DocumentsAdapter)list.getAdapter()).setDocType(WSOrderDoc.instance());
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnSend)
			send();
	}

	private void send() {
		new DocTypeSender(this, findViewById(R.id.btnSend), WSOrderDoc.instance()).execute((Void[])null);
	}

	@Override
	public void postSendExecute(boolean result) {
		refreshContent();
	}
}
