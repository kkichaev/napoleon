package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ModifyOrgImpl;
import com.grsoft.dataobjects.impl.NewOrgImpl;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.gps.GPSUtilNew;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class OrgsList extends Activity implements OnClickListener, android.content.DialogInterface.OnClickListener, OnItemClickListener {
	private View btnNew;
	private ListView list;
	private OrgsListAdapter adapter;
	public static void open(Context context) {
		Intent i = new Intent(context, OrgsList.class);
		context.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.orgslist);
		
		list = (ListView) findViewById(R.id.list);
		btnNew = findViewById(R.id.btnNewDoc);
		
		adapter = new OrgsListAdapter(this);
		
		btnNew.setOnClickListener(this);
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		
		registerForContextMenu(list);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		adapter.reload();
		adapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnNewDoc)
			showDialog(R.id.new_client_dlg);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == R.id.new_client_dlg)
			return createNewClientDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createNewClientDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(new String[] {getString(R.string.neworg), getString(R.string.modifyorg)}, this);
		return builder.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == 0)
			doNewOrg();
		else
			doModifyOrg();
	}

	private void doModifyOrg() {
		ModifyOrgImpl doc = new ModifyOrgImpl();
		
		if (doc.init(this, "", GPSUtilNew.getLastKnownLocation()))
			doc.open(this);
	}

	private void doNewOrg() {
		NewOrgImpl doc = new NewOrgImpl();
		
		if (doc.init(this, "", GPSUtilNew.getLastKnownLocation()))
			doc.open(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		CreatableDocument<?> d = (CreatableDocument<?>) parent.getItemAtPosition(position);
		d.open(this);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		getMenuInflater().inflate(R.menu.orglistmenu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		boolean res = super.onContextItemSelected(item);
		AdapterView.AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();		
		Document<?> doc = (Document<?>) adapter.getItem(menuInfo.position);
		
		if (item.getItemId() == R.id.itDelete){
			doc.delete();
			adapter.reload();
			adapter.notifyDataSetChanged();
			res = true;
		}
			
		return res;
	}
}
