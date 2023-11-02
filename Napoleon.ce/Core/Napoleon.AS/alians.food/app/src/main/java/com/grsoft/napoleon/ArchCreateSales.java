package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.grsoft.dataobjects.impl.ArchSalesImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.ExtrasConst;


public class ArchCreateSales extends CreateSalesEx {
	public static void open(Context ctx, Document<?> doc, boolean editOldDoc) {
		Intent i = new Intent(ctx, ArchCreateSales.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(ExtrasConst.EDIT_MODE_STR, editOldDoc);
		ctx.startActivity(i);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		salesImpl = new ArchSalesImpl();
		super.onCreate(savedInstanceState);
	}
}
