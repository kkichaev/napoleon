package com.grsoft.napoleon;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.grsoft.napoleon.documents.QuestionDoc;
import com.grsoft.network.ObjectListener;

public class UpdateDBEx extends UpdateDB {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.cbPresent).setVisibility(View.GONE);
		CheckBox cb = (CheckBox) findViewById(R.id.cbRemains);
		cb.setChecked(false);
		cb.setVisibility(View.GONE);
	}
	
	@Override
	public List<ObjectListener> getExported() {
		List<ObjectListener> result = super.getExported();;
		result.add(0,QuestionDoc.instance().getDirtyDocuments());
		return result; 
	}
}
