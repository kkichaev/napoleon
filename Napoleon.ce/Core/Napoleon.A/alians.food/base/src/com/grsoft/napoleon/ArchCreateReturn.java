package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ArchReturnImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class ArchCreateReturn extends CreateReturnEx {
	public static void open(Context ctx, Document<?> doc, boolean editOldDoc) {
		Intent i = new Intent(ctx, ArchCreateReturn.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldDoc);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		doc = new ArchReturnImpl();
		super.onCreate(savedInstanceState);
	}
}
