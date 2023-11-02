package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.widget.CheckBox;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.OrgRestRcvr;
import com.grsoft.database.ReturnRcvr;
import com.grsoft.dataobjects.FolderCoef;
import com.grsoft.napoleon.documents.OrderDoc;
import com.grsoft.napoleon.documents.RemnantsDoc;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {

	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();

		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
	}

	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new Hitching(FolderCoef.class, "FolderCoef"));
		return ret;
	}
	
	@Override
	protected List<Hitching> getDebetHitching() {
		List<Hitching> ret = super.getDebetHitching();
		ret.add(new ReturnRcvr());
		ret.add(new OrgRestRcvr());
		return ret;
	}
	
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = new ArrayList<Hitching>();
		result.add(new DocumentRestore(OrderDoc.instance()));
		result.add(new DocumentRestore(RemnantsDoc.instance(), "OrgRemnants", "date"));
		return result;
	}
}
