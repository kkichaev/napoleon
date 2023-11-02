package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.ReturnEx;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CreateReturnEx extends CreateReturn {
	private static final int CLEAR_ITEMS = 0x123;
	int sel = -1;
	
	@Override
	int getContentViewID() {
		return R.layout.createreturnex;
	}
	
	@Override
	protected void initView() {
		ReturnEx r = (ReturnEx) doc.getData();
		OrgEx o = (OrgEx) oi.getData();
		loadDogovors(o, r.dogovor);
	}
	
	void loadDogovors(OrgEx org, String selDog) {
		List<OrgDogovor> dg = new ArrayList<OrgDogovor>();
		sel = -1;
		for(OrgDogovor od : org.dogovors) {
			if(od.id.equals(selDog)) {
				sel = dg.size();
			}
			dg.add(od);
		}
		
		Spinner s = (Spinner)findViewById(R.id.spDogovor);
		ArrayAdapter<OrgDogovor> aa = new ArrayAdapter<OrgDogovor>(this, R.layout.simple_spinner_layout, dg);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		s.setAdapter(aa);
		if( sel >= 0 )
			s.setSelection(sel);
		
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if(id == CLEAR_ITEMS) {
			AlertDialog.Builder b = new AlertDialog.Builder(this);
			b.setTitle("Предупреждение");
			b.setMessage("Изменен договор. Очистить возврат?");
			b.setNegativeButton(android.R.string.no, null);
			b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					clearReturn();

					if(!editMode)
						Warehouse.open(CreateReturnEx.this, doc, false);

					finish();
				}
				
			});
			return b.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void clearReturn() {
		Return r = doc.getData();
		r.items.clear();
		updateReturn(r);
		doc.write();
	}
	
	@Override
	protected boolean canChange() {
		Spinner s = (Spinner)findViewById(R.id.spDogovor);
		OrgDogovor sel = (OrgDogovor) s.getSelectedItem();
		if(sel != null) {
			ReturnEx r = (ReturnEx)doc.getData();
			if(r.dogovor.equals(sel.id) == false && r.items.size() > 0) {
				//showDialog(CLEAR_ITEMS)
				clearReturn();
				return false;
			}
		}
		return super.canChange();
	}
	
	@Override
	protected void updateReturn(Return r) {
		super.updateReturn(r);
		Spinner s = (Spinner)findViewById(R.id.spDogovor);
		OrgDogovor sel = (OrgDogovor) s.getSelectedItem();
		if(sel != null)
			((ReturnEx)r).dogovor = sel.id;
	}
}
