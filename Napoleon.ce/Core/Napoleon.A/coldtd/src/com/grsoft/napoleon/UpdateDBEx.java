package com.grsoft.napoleon;

import java.util.ArrayList;

import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.grsoft.database.Hitching;
import com.grsoft.database.OrgHitching;
import com.grsoft.dataobjects.Org;
import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RawObject;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDBPrint {
	@Override
	protected Hitching getOrgHitching() {
		return new OrgExHitching();
	}


	@Override
	protected void onResume() {
		super.onResume();

		Spinner sp = (Spinner) findViewById(R.id.spMonthRecreate);
		if (sp != null) {
			((CheckBox)findViewById(R.id.cbRecreateStory)).setText("Восстановить заявки, дней");
			
			int sel = -1;
			CfgNpl config = (CfgNpl) ConfigManager.getConfig();
			ArrayList<String> values = new ArrayList<String>();
			for(int  i=0; i < 45; i++ ) {
				if( i == config.daysToRecreate )
					sel = values.size();
				values.add(Integer.toString(i));
			}

			ArrayAdapter<String> aa = new ArrayAdapter<String>(this, R.layout.simple_spinner_layout, values);
			aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
			sp.setAdapter(aa);

			if( sel >= 0 )
				sp.setSelection(sel);
		}
	}
	
	@Override
	protected void saveSettings() {
		Spinner sp = (Spinner) findViewById(R.id.spMonthRecreate);
		if (sp != null) {
			CfgNpl c = (CfgNpl) ConfigManager.getConfig();
			c.monthsToRecreate = 0;
			c.daysToRecreate = Integer.parseInt((String) sp.getSelectedItem());
			ConfigManager.save();
		}
	}
}

class OrgExHitching extends OrgHitching {
	@Override
	public void onRead(RawObject rawObject) throws RuntimeException {
		Org dobj = (Org)rawObject.createDataObject(dataObject);
		dobj.srchName = dobj.name.toUpperCase() + "|" + dobj.address.toUpperCase() + "|" + dobj.id.toUpperCase();
		dbProxy.insertRecord(dobj);
	}
}
