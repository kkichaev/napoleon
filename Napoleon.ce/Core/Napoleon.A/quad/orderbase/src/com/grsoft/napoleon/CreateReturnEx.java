package com.grsoft.napoleon;

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;

public class CreateReturnEx extends CreateReturn {
	@Override int getContentViewID() { return R.layout.createreturnex; }

	@Override
	protected void initView() {
		ReturnEx re = (ReturnEx)doc.getData();
		int selected = -1;
		ArrayList<FirmEx> firms = new ArrayList<FirmEx>();
		FirmEx fe = new FirmEx();
		DbReader r = new DbReader();
		String table = DataObjectInfo.getInstance().getTableName(fe.getClass());
		boolean bdo = r.select(fe, table, null, "name");
		while(bdo != false) {
			if( fe.id.equals(re.firma))
				selected = firms.size();
			firms.add(fe);
			
			fe = new FirmEx();
			bdo = r.selectNext(fe);
		}
		r.close();
		
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		ArrayAdapter<FirmEx> fa = new ArrayAdapter<FirmEx>(this, R.layout.simple_spinner_layout, firms);
		spFirma.setAdapter(fa);
		if( selected >= 0 )
			spFirma.setSelection(selected);
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		Spinner spFirma = (Spinner) findViewById(R.id.spFirma);
		FirmEx fe = (FirmEx) spFirma.getSelectedItem();
		if( fe != null)
			((ReturnEx)r).firma = fe.id;
	}
}
