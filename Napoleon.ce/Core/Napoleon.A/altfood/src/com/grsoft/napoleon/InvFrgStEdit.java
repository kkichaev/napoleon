package com.grsoft.napoleon;

import com.grsoft.dataobjects.Visit;
import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class InvFrgStEdit extends VisitEdit {
	public static void open(Context context, long rowid){
		Intent i = new Intent(context, InvFrgStEdit.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, rowid);
				
		context.startActivity(i);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected CreatableDocument<? extends Visit> createDocument() {
		return (CreatableDocument<? extends Visit>) DocType.getCurDoc().create();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		btnSend.setVisibility(View.GONE);
		
		TextView tv = (TextView) findViewById(R.id.tvTitle);
		tv.setText(DocType.getCurDoc().getDocTitle());
	}
	
	@Override protected int getContentView() { return R.layout.invstedit;	}
}
