package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ReqNewOrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.ReqNewOrgDoc;
import com.grsoft.util.GpsCoord;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.Refreshable;

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

public class ReqNewOrgList extends BaseActivity implements Refreshable{
	private ListView list;
	
	public static void open(Context context) {
		Intent i = new Intent(context, ReqNewOrgList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.req_doc_list);
		
		View btnNewDoc = findViewById(R.id.btnNewDoc);
		list = (ListView) findViewById(R.id.lvDocs);
		
		btnNewDoc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ReqNewOrgImpl order = new ReqNewOrgImpl();
				
				if(order.init(v.getContext(), null,GpsCoord.empty))
					order.open(v.getContext());
			}
		});
		
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ReqNewOrgImpl o = (ReqNewOrgImpl) parent.getAdapter().getItem(position);
				o.open(view.getContext());
			}
		});
		registerForContextMenu(list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		list.setAdapter(new DocumentsAdapter(this, ReqNewOrgDoc.instance(), "", "created desc"));
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
		((DocumentsAdapter)list.getAdapter()).setDocType(ReqNewOrgDoc.instance());
	}
}
