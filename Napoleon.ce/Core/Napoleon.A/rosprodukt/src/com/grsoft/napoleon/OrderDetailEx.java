package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderDetailEx extends OrderDetail {
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
}
