package com.grsoft.napoleon;

import java.util.Map.Entry;

import com.grsoft.napoleon.IncassDebDistrEdit.DlvKey;

import android.widget.Toast;

public class IncassDebDistrEditEx extends IncassDebDistrEdit {
	@Override
	protected void send() {
		if( !docHaveErrors())
			super.send();
	}
	
	@Override
	public void onBackPressed() {
		if( !docHaveErrors())
			super.onBackPressed();
	}
	
	boolean docHaveErrors() {
		if(doc.isEditable() &&  !autoMode) {
			long sm = getSum();
			long sumdstr = 0;
			for(Entry<DlvKey, Long> kv : sums.entrySet())
				sumdstr += kv.getValue();
			if( sm != sumdstr) {
				Toast.makeText(this, "Распределите сумму по накладным", Toast.LENGTH_SHORT).show();
				return true;
			}
		}
		return false;
	}
}
