package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;

import com.grsoft.dataobjects.impl.RestockImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.RestockDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.Refreshable;

public class RestockList extends BaseActivity implements Refreshable {
	ListView list;
	
	public static void open(Context context) {
		Intent i = new Intent(context, RestockList.class);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restock_list);
		
		list = (ListView) findViewById(R.id.lvDocs);
		
		findViewById(R.id.btnNewDoc).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RestockImpl doc = new RestockImpl();
				if( doc.init(RestockList.this, "", GpsCoord.empty) )
					doc.open(RestockList.this);
			}
		});
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				RestockImpl o = (RestockImpl) parent.getAdapter().getItem(position);
				o.open(view.getContext());
			}
		});
		registerForContextMenu(list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		list.setAdapter(new DocumentsAdapter(this, RestockDoc.instance(), "", "created desc"));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		Adapter a = list.getAdapter();
		if(a != null && a instanceof DocumentsAdapter)
			((DocumentsAdapter)a).close();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
			getMenuInflater().inflate( R.menu.doc_context_menu, menu);
			menu.removeItem(R.id.itCopy);
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
		}
		return true;
	}
	
	protected void docDelete(CreatableDocument<?> doc) {
		DocDeleteHelper.delete(doc, this);
	}

	@Override
	public void refreshContent() {
		((DocumentsAdapter)list.getAdapter()).setDocType(RestockDoc.instance());
	}
}
