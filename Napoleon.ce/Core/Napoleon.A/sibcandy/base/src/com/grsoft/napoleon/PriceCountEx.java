package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.util.ExtrasConst;

public class PriceCountEx extends PriceCount {
	private static final String REMOVE_EMPTY = "REMOVE_EMPTY";
	private boolean removeEmpty = false;

	public static void open(Context context, long priceRoid,
			DbObject<? extends DataObject> doc, boolean removeEmpty) {
		Intent i = new Intent(context, activity);

		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(REMOVE_EMPTY, removeEmpty);

		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		removeEmpty = getIntent().getExtras().getBoolean(REMOVE_EMPTY);
	}

	@Override
	protected boolean getStartInPack() {
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && removeEmpty
				&& document instanceof ReturnImpl) {
			if (((ReturnImpl)document).getData().items.size() == 0)
				document.delete();
		}

		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(removeEmpty && document instanceof ReturnImpl )
			if (((ReturnImpl)document).getData().items.size() == 0)
				document.delete();
	}
	
}
