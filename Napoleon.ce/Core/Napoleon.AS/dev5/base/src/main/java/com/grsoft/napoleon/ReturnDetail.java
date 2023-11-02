package com.grsoft.napoleon;
import com.grsoft.aceteam.R;

import com.grsoft.dataobjects.Return;
import com.grsoft.dataobjects.impl.ReturnImplBase;
import com.grsoft.napoleon.documents.DocType;
import com.grsoft.napoleon.documents.ReturnDoc;
import com.grsoft.util.ExtrasConst;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ReturnDetail extends OrderDetail {
	public static Class<? extends Activity> activity = ReturnDetail.class;
	
	public static void open(Context ctx, ReturnImplBase<? extends Return> doc) {
		Intent i = new Intent(ctx, activity);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		ctx.startActivity(i);
	}
	
	@Override protected boolean haveFocusedGroup() { return false; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if( DocType.getCurDoc() != ReturnDoc.instance() )
			DocType.setCurDoc(ReturnDoc.instance());
		
		super.onCreate(savedInstanceState);
		setTitle(R.string.remains_doc_title);
	}
}
