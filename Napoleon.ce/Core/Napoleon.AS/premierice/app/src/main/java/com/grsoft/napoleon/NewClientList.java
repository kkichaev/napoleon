package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.impl.NewClientImpl;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocumentSender;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.NewClientDoc;
import com.grsoft.napoleon.documents.SendResultListener;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.network.DocExportListener;
import com.grsoft.view.Refreshable;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

public class NewClientList extends Activity implements Refreshable, SendResultListener {
	
	Adapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.new_client_list);
		
		findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				NewClientEdit.open(NewClientList.this, null);
			}
		});
		
		findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View arg0) { send(); }
		});
		
		adapter = new Adapter(this);
		ListView lv;
		lv = (ListView)findViewById(R.id.lvItems);
		lv.setAdapter(adapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				NewClientImpl doc = (NewClientImpl) adapter.getItem(arg2);
				doc.open(NewClientList.this);
			}
		});
		registerForContextMenu(lv);
	}
	
	protected void send() {
		DocExportListener del = NewClientDoc.instance().getDirtyDocuments();
		if(del.getDocuments().getCount() > 0) {
			List<DocExportListener> sends = new ArrayList<DocExportListener>();
			sends.add(del);

			DocExportListener photos = VisitDoc.instance().getDirtyDocuments();
			if(photos.getDocuments().getCount() > 0)
				sends.add(photos);

			new DocumentSender(this, findViewById(R.id.btnSend), sends, this).execute((Void[])null);
		}
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.new_client_context, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		NewClientImpl doc = (NewClientImpl) adapter.getItem(menuInfo.position);
		if(item.getItemId() == R.id.itDelete && doc != null) {
			DocDeleteHelper.delete(doc, this);
		}
		return super.onContextItemSelected(item);
	}	
	
	@Override
	protected void onResume() {
		super.onResume();
		adapter.setDocType(NewClientDoc.instance());
	}
	
	class Adapter extends DocumentsAdapter {
		public Adapter(Context context) {
			super(context, NewClientDoc.instance(), null, "created desc");
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View ret = super.getView(position, convertView, parent);
			ret.findViewById(R.id.tvSum).setVisibility(View.GONE);
			return ret;
		}
	}

	@Override
	public void refreshContent() {
		adapter.notifyDataSetChanged();
	}

	@Override
	public void postSendExecute(boolean result) {
		if(result) {
			adapter.setDocType(NewClientDoc.instance());
		}
		
	}
}
