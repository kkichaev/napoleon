package com.grsoft.napoleon;

import com.grsoft.dataobjects.CheckStatusHandler;
import com.grsoft.dataobjects.ChekBase;
import com.grsoft.dataobjects.CommonChekItem;
import com.grsoft.dataobjects.CommonIncassItem;
import com.grsoft.dataobjects.impl.CommonCheckImpl;
import com.grsoft.dataobjects.impl.CommonIncassImpl;
import com.grsoft.dataobjects.impl.CommonIncassImplBase;

import android.content.Context;
import android.content.Intent;

public class CommonChekList extends CommonIncassList {
	public static void open(Context context) {
		Intent intent = new Intent(context, CommonChekList.class);
		context.startActivity(intent);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Class<? extends CommonIncassImplBase> documentType() { return CommonCheckImpl.class; }

	@Override
	protected void deleteItem(long rowid) {
		CommonCheckImpl ci = new CommonCheckImpl();
		ci.read(rowid);
		if(!ci.isExported()) {
			CheckStatusHandler cch = new CheckStatusHandler();
			for(CommonIncassItem cii : ci.getData().items) {
				cch.update(((CommonChekItem)cii).created, ChekBase.CHEK_COMMITED);
			}
			cch.close();
		}
		ci.delete();
		ci.close();
		((CILAdapter)list.getAdapter()).reload();

//		super.deleteItem(rowid);
	}
}
