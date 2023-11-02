package com.grsoft.napoleon;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.FirmEx;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;
import com.grsoft.dataobjects.impl.OrgDogovorImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.modules.print.util.DocHelper;

public class CreateReturnEx extends CreateReturn {
	
	@Override int getContentViewID() { return R.layout.createreturnex; }
	
	@Override
	protected void init() {
		ReturnEx r = (ReturnEx)doc.getData();
		OrgDogovorImpl.loadFirms((Spinner)findViewById(R.id.spFirma),
				(Spinner)findViewById(R.id.spDog),
				r.ido, r.firmCode, r.dogovor);
	
		OrgImpl oi = new OrgImpl();
		OrgEx org = (OrgEx) oi.getData();
		org.id = r.id;
		oi.read();
		oi.close();
		
		CostList values = new CostList();
		values.loadCost(org);
		
		EditText ed = (EditText)findViewById(R.id.edNumber);
		ed.setText(r.number);
		ed.setEnabled(false);

		Spinner spCost = (Spinner)findViewById(R.id.spPrices);
		ArrayAdapter<CostData> aa = new ArrayAdapter<CostData>(this, R.layout.simple_spinner_layout, values);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		spCost.setAdapter(aa);
		
		spCost.setEnabled(false);
		
		int index = 0;
		for(CostData cd : values) {
			if( cd.id.equals(r.costCode) ) {
				spCost.setSelection(index);
				break;
			}
			index++;
		}
	}
	
	@Override
	protected void init(Return r, Org data) {
		ReturnEx re = (ReturnEx) r;
		OrgEx oe = (OrgEx)data;
		
		re.ido = oe.ido;
	}
	
	@Override
	protected void updateReturn(Return r) {
		ReturnEx re = (ReturnEx) r;
		if( !editMode )
			DocHelper.saveDocNumber(DataObjectInfo.getInstance().getTableName(Return.class), re.number);

		FirmEx fe = (FirmEx)((Spinner) findViewById(R.id.spFirma)).getSelectedItem();
		if( fe != null )
			re.firmCode = fe.id;
		
		OrgDogovor od = (OrgDogovor)((Spinner)findViewById(R.id.spDog)).getSelectedItem();
		if( od != null )
			re.dogovor = od.idDog;

		EditText ed = (EditText)findViewById(R.id.edNumber);
		re.number = ed.getText().toString();

		r.remark = ((EditText)findViewById(R.id.edNotes)).getText().toString();
		
		Spinner spCost = (Spinner)findViewById(R.id.spPrices);
		CostData cd = (CostData) spCost.getSelectedItem();
		if( cd != null ) {
//			CostList.changeItemCost(re, cd.id, cd.index);
			re.costCode = cd.id;
			re.sumType = cd.index;
		}
	}
}
