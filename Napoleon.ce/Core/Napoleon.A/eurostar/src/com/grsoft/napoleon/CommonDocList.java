package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocTypeBase;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.RkoDoc;
import com.grsoft.util.ExtrasConst;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.BaseActivity;

public class CommonDocList extends BaseActivity {
	DocType docType;
	DocumentsAdapter adapter;
	
	public static void open(Context c, DocTypeBase doc) {
		Intent i = new Intent(c, CommonDocList.class);
		i.putExtra(ExtrasConst.DOC_TYPE, doc.getObjectName());
		c.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_docs);
		
		Bundle b = (savedInstanceState == null) ? getIntent().getExtras() : savedInstanceState;
		
		String docTypeName = b.getString(ExtrasConst.DOC_TYPE);
		if( docTypeName == null )
			docTypeName = RkoDoc.instance().getObjectName();
		
		docType = (DocType) DocTypeBase.getDocType(docTypeName);
		docType.viewOpened(this);
		
		TextView tv = (TextView)findViewById(R.id.tvTitle);
		tv.setText(docType.getName());
		
		findViewById(R.id.btnNewDoc).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CreatableDocument<?> doc = (CreatableDocument<?>)docType.create();
				if( doc.init(CommonDocList.this, "", GPSUtilNew.getLastKnownLocation()) )
					doc.open(CommonDocList.this);
				doc.close();
			}
		});
		ListView lv = (ListView)findViewById(R.id.lvDocs);
		lv.setDividerHeight(0);
		registerForContextMenu(lv);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.doc_context_menu, menu);
		menu.removeItem(R.id.itCopy);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
		
		if (item.getItemId() == R.id.itDelete) {
			doc.delete();
		} else if (item.getItemId() == R.id.itEdit) {
			doc.open(this);
		}
		return true;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		adapter.close();
	}
	
	@Override
	protected void onResume() {
		super.onResume();

		ListView lv = (ListView)findViewById(R.id.lvDocs);
		if( adapter != null )
			adapter.close();
		
		adapter = new DocumentsAdapter(this, docType, null, "date desc, created desc");
		lv.setAdapter(adapter);
		lv.setOnItemClickListener( adapter.clickListner() );
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ExtrasConst.DOC_TYPE, docType.getObjectName());
	}
}
