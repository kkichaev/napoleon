package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.widget.CheckBox;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.napoleon.documents.ReturnDoc;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = super.getRestoreHitching();
		result.add(new DocumentRestore(ReturnDoc.instance()));
		return result;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox cbRemains = (CheckBox) findViewById(R.id.cbRemains);
		cbRemains.setChecked(false);
	}
}
