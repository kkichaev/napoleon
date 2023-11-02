package com.grsoft.napoleon;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.Visit;
import com.grsoft.dataobjects.impl.VisitImpl;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.VisitDoc;
import com.grsoft.napoleon.util.HorizontalListView;
import com.grsoft.napoleon.util.ImagesAdapter;
import com.grsoft.napoleon.util.PhotoClickHandler;
import com.grsoft.napoleon.util.PhotoClickHandler.EventHandler;
import com.grsoft.util.Util;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderDetailEx extends OrderDetail implements EventHandler {
	
	VisitImpl visit;
	String picPath;
	ImagesAdapter adapter;
	boolean inited = false;
	
	private static final int CHANGE_ORG = 0x100;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if( !super.onCreateOptionsMenu(menu) )
			return false;
		
		if( doc.isExported() == false )
			menu.add(Menu.NONE, CHANGE_ORG, Menu.NONE, "Изменить контрагента");
		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == CHANGE_ORG ) {
			final List<KeyValue> values = new ArrayList<KeyValue>();
			
			Org o = new Org();
			String table = DataObjectInfo.getInstance().getTableName(o.getClass());
			DbReader r = new DbReader();
			r.setReadingFields("id,name");
			int selected = -1;
			boolean bdo = r.select(o, table, null, "name");
			while(bdo) {
				KeyValue kv = new KeyValue(o.id, o.name);
				if( o.id.equals(doc.getId()))
					selected = values.size();
				
				values.add(kv);
				bdo = r.selectNext(o);
			}
			r.close();
			
			ArrayAdapter<KeyValue> a = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values); 
			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Выберите контрагента");
			builder.setSingleChoiceItems(a, selected, new DialogInterface.OnClickListener() {				
				@Override public void onClick(DialogInterface dialog, int which) { changeOrg(values.get(which).key.toString()); }
			});
			return builder.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void changeOrg(String newId) {
		doc.getData().id = newId;
		doc.write();
		doc.open(this);
		try {
			OrderDoc.instance().refreshDocSum();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finish();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if( item.getItemId() == CHANGE_ORG ) {
			if( doc.isExported() == false )
				showDialog(CHANGE_ORG);
			return false;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void setContentView() {
		setContentView(R.layout.orderdetailex);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		openAssociatedVisit();
		adapter = new ImagesAdapter(this, visit);
		
		HorizontalListView g = (HorizontalListView)findViewById(R.id.gvItems);
		g.setAdapter(adapter);
		
		findViewById(R.id.btnPhoto).setOnClickListener(new PhotoClickHandler(visit, this, VisitDoc.instance()));
	}

	private void openAssociatedVisit() {
		visit = new VisitImpl();
		Order o = doc.getData();
		long created = o.created.getTime() + 1000;
		Date dt = new Date(created);
		visit.getData().created = dt;
		
		if( visit.read() == false ) {
			Visit v = visit.getData();
			v.date = Util.getDateTime();
			v.created = dt;			
			v.id = doc.getId();
			v.latitude = o.latitude;
			v.longitude = o.longitude;
			v.params = 0;
			
			visit.write();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if( inited ) {
			visit.read(visit.getRowid(), false);
			adapter.notifyDataSetChanged();
		}
		inited = true;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Visit v = visit.getData();
		boolean canDelete = (v.remark.trim().length() == 0 && v.items.size() == 0);
		if( canDelete )
			visit.delete();
		visit.close();
	}

	@Override
	public void prepareBoforeClick() {
	}

	@Override
	public void makePhotoFile(File newFile) {
		picPath = newFile.getAbsolutePath();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == PhotoClickHandler.CAMERA_ACTIVITY && resultCode == RESULT_OK){
			if(picPath != null && picPath.trim().length() > 0) {
				visit.addPhoto(picPath.getBytes());
				adapter.notifyDataSetChanged();
			}
		}
	}
}
