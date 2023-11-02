package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.napoleon.documents.BonusDoc;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;

public class BonusDetail extends OrderDetail {
	public static void open(Context context, BonusImpl doc) {
		Intent i = new Intent(context, BonusDetail.class);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		context.startActivity(i);		
	}
	
	@Override
	protected void setDocType() {
		docType = BonusDoc.instance();
	}
}
