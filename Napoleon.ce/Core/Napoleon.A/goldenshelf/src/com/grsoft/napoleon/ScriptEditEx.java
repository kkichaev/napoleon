package com.grsoft.napoleon;

import android.view.View;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.script.ScriptEdit;
import com.grsoft.script.dataobjects.ScriptDefItem;
import com.grsoft.script.dataobjects.impl.ScriptImpl;


public class ScriptEditEx extends ScriptEdit {

	protected void onResume() {
		super.onResume();
		findViewById(R.id.tvTotalSum).setVisibility(View.GONE);
	};
	
	protected CreatableDocument<?> openFirstItem(ScriptImpl scriptImpl, ScriptDefItem item, DocType dt) {return null;}
}
