package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Firm;
import com.grsoft.util.view.dialog_helper.KeyValue;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class FirmHelper {
	
	static public void loadFirms(Spinner spFirma, String selKeys) {
		ArrayList<KeyValue> firms = new ArrayList<KeyValue>();
		Firm fi = new Firm();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(fi.getClass());
		boolean bdo = r.select(fi, table, null, "name");
		int selected = -1;
		while( bdo ) {
			KeyValue kv = new KeyValue(fi.id, fi.name);
			if( selKeys.equals(fi.id) )
				selected = firms.size();
			firms.add(kv);
			bdo = r.selectNext(fi);
		}
		r.close();
		ArrayAdapter<KeyValue> aa = new ArrayAdapter<KeyValue>(spFirma.getContext(), R.layout.simple_spinner_layout, firms);
		spFirma.setAdapter(aa);
		if( selected >= 0 )
			spFirma.setSelection(selected);
	}
}
