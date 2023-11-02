package com.grsoft.napoleon;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.Accounts;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.IncassEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.TypeName;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.util.CommentChoice;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class IncassEditEx extends IncassEdit {
	CommentChoice commentChoice;
	
	@Override
	protected int getContentViewID() { return R.layout.incassex; }
	
	@Override
	protected void init(Bundle bundle) {
		super.init(bundle);
		
		btnSend.setVisibility(View.GONE);
	
		IncassEx incass = (IncassEx) doc.getData();
		if( incass.ido == null || incass.ido.length() == 0 ) {
			OrgImpl oi = new OrgImpl();
			oi.getData().id = incass.id;
			oi.read();
			oi.close();
			incass.ido = ((OrgEx)oi.getData()).ido;
		}
		
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbChek);
		cb.setChecked( incass.chek > 0 );			
	
		View ok = findViewById(R.id.btnOK);
		ok.setVisibility(View.VISIBLE);
		ok.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				save();
				finish();
			}
		});
		
		loadSpinner(R.id.spAccounts, new Accounts(), incass.account, "ido='" + incass.ido + "'");
		
		EditText ed = (EditText)findViewById(R.id.edRemark);
		commentChoice = new CommentChoice(ed);
	}
	
	private void loadSpinner(int id, TypeName data, String key, String where) {
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		int selected = -1;
		String table = DataObjectInfo.getInstance().getTableName(data.getClass());
		DbReader r = new DbReader();
		boolean bdo = r.select(data, table, where, "name");
		while( bdo ) {
			if(data.type.equals(key))
				selected = values.size();
			
			values.add(new KeyValue(data.type, data.name));
			bdo = r.selectNext(data);
		}
		
		Spinner sp = (Spinner)findViewById(id);
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		sp.setAdapter(aa);
		if( selected >= 0 && selected < sp.getCount())
			sp.setSelection(selected);
	}

	@Override
	protected void setDocument() {
		super.setDocument();

		IncassEx incass = (IncassEx) doc.getData();
		CheckBox cb;
		cb = (CheckBox)findViewById(R.id.cbChek);
		incass.chek = (cb.isChecked()) ? 1 : 0;
		
		KeyValue value = (KeyValue) ((Spinner)findViewById(R.id.spAccounts)).getSelectedItem();
		if( value != null )
			incass.account = value.key.toString();
	}
}
