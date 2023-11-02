package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.grsoft.dataobjects.OrgSum;
import com.grsoft.dataobjects.impl.MovementImpl;
import com.grsoft.dataobjects.impl.OrgSumImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocDeleteHelper;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.DocumentsAdapter;
import com.grsoft.napoleon.documents.MovementDoc;
import com.grsoft.util.gps.GPSUtilNew;
import com.grsoft.view.BaseActivity;
import com.grsoft.view.Refreshable;

public class MovementList extends BaseActivity implements Refreshable {
	ImageButton newDoc;
	ListView list;
	private DocumentsAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.movement_list);
		newDoc = (ImageButton) findViewById(R.id.btnNewDoc);
		newDoc.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MovementImpl movement = new MovementImpl();
				if(movement.init(v.getContext(), "", GPSUtilNew.getLastKnownLocation()))
					movement.open(v.getContext());
			}
		});
		
		list = (ListView) findViewById(R.id.lvDocs);
		registerForContextMenu(list);
	}

	public static void open(Context context) {
		Intent i = new Intent(context, MovementList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		DocType.setCurDoc(MovementDoc.instance());
		
		adapter = new DocumentsAdapter(this, MovementDoc.instance(), "", "created DESC "); 
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter.clickListner());
		
		refreshTotalSum();
	}
	
	protected void refreshTotalSum() {
		OrgSumImpl oi = new OrgSumImpl();
		OrgSum os = oi.getData();
		os.id = "";
		os.type = DocType.getCurDoc().getName();
		oi.read();
		oi.close();
		updateTotalSum(os.sum, 0);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.doc_context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		CreatableDocument<?> doc = (CreatableDocument<?>) adapter.getItem(menuInfo.position);
		if( doc != null ) {
			if (item.getItemId() == R.id.itDelete) {
				docDelete(doc);
			} else if (item.getItemId() == R.id.itEdit) {
				doc.open(this);
			} else if (item.getItemId() == R.id.itCopy) {
				CreatableDocument<?> cd = doc.copy();
				if( cd != null )
					cd.open(this);
				else 
					Toast.makeText(this, R.string.copy_doesnt_allow, 
							Toast.LENGTH_LONG).show();
			}
		}
		try
		{
			DocType.getCurDoc().refreshDocSum("");
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
		
		return super.onContextItemSelected(item);
	}
	
	protected void docDelete(CreatableDocument<?> doc) {
		DocDeleteHelper.delete(doc, this);
	}
	
	@Override
	public void refreshContent() {
		adapter.setDocType(MovementDoc.instance());
		refreshTotalSum();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if( adapter != null )
			adapter.close();
	}
	
}
