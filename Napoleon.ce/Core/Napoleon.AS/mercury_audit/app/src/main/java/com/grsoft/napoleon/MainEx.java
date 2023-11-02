package com.grsoft.napoleon;

import java.util.UUID;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.Citylmpl;
import com.grsoft.napoleon.documents.DocType;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Adapter;
import android.widget.EditText;

public class MainEx extends Main implements OnClickListener{
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		findViewById(R.id.btnNewItem).setOnClickListener(this);
	}
	
	@Override
	protected int getResourceID() {
		return R.layout.mainex;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		int pos = ((AdapterContextMenuInfo)menuInfo).position;
		Adapter a = list.getAdapter();
		
		if(a != null && a instanceof BaseMainAdapter){
			BaseMainAdapter bma = (BaseMainAdapter)a;
			Org o = bma.getOrg(pos);
			
			if(o != null){
				menu.add(0, R.id.itShowMap, menu.size(), R.string.show_on_map);
				menu.add(0, R.id.itEdit, menu.size(), R.string.edit);
			}
		}
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.itEdit) {
			editItem(((AdapterContextMenuInfo)item.getMenuInfo()).position);
			return true;
		} else
			return super.onContextItemSelected(item);
	}
	
	private void editItem(int position) {
		if(((OrgAdapter)list.getAdapter()).isTop())
			editCity(position);
		else
			editOrg(position);
	}

	private void editOrg(int position) {
		OrgAdapter a = ((OrgAdapter)list.getAdapter()); 
		Org org = a.getOrg(position);
		String city = a.getCityName(); 
		OrgEdit.openForResult(this, org.id, city);
	}

	private void editCity(int position) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void postInit() {
		super.postInit();
		
		list.setAdapter(new OrgAdapter(this));
	}

	@Override
	protected void adjustViewForDocType(DocType docType) {
		super.adjustViewForDocType(docType);
		tvTotalSum.setVisibility(View.GONE);
		findViewById(R.id.tvMainDocValColTitle).setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btnNewItem) {
			if(((OrgAdapter)list.getAdapter()).isTop())
				showDialog(R.id.new_city_dlg);
			else {
				OrgAdapter a = ((OrgAdapter)list.getAdapter()); 
				String city = a.getCityName(); 
				OrgEdit.openForResult(this, null, city);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == R.id.org_edit_id && resultCode == RESULT_OK) {
			((OrgAdapter)list.getAdapter()).putOrgItem(data.getStringExtra(OrgEdit.ORG_ID));
		}
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == R.id.new_city_dlg)
			return createNewCityDlg();
		else
			return super.onCreateDialog(id);
	}

	private Dialog createNewCityDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.city);
		builder.setView(View.inflate(this, R.layout.edit_city_dlg, null));
		builder.setPositiveButton(R.string.ok, newCityOKClick);
		builder.setNegativeButton(R.string.cancel, null);
		return builder.create();
	}

	private DialogInterface.OnClickListener  newCityOKClick = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			EditText ed = (EditText) ((AlertDialog)dialog).findViewById(R.id.edName);
			
			Citylmpl c = new Citylmpl();
			c.getData().id =  UUID.randomUUID().toString().replace("-", "");
			c.getData().name = ed.getText().toString().trim();
			c.getData().flags = Org.FL_USER_CREATED;
			c.write();
			c.close();
			
			((OrgAdapter)list.getAdapter()).putCityItem(c);
		}
	};
}
