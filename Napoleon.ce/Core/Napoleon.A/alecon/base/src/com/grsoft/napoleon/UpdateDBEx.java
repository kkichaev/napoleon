package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.widget.CheckBox;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.napoleon.documents.IncassDoc;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.napoleon.documents.VisitDoc;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		((CheckBox) findViewById(R.id.cbRemains)).setChecked(false);
	}
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = super.getRestoreHitching();
		// абанов провил убрать ѕосещени€ из восстановлени€ за€вок
		//письмо от 2013.09.13 на kkichaev@pochta.ru тема Alekon
		//result.add(new DocumentRestore(VisitDoc.instance()));
		result.add(new DocumentRestore(ReturnDoc.instance()));
		result.add(new DocumentRestore(IncassDoc.instance()));
		return result;
	}
}
