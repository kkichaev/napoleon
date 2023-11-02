package com.grsoft.napoleon;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.util.ExtrasConst;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

public class PriceCountEx extends PriceCount {
	
	static final String INPACK_TAG = "IN_PACK";
	
	Boolean startInpack = null;
	
	public static void openEx(Context context, long priceRoid, DbObject<? extends DataObject> doc, boolean inPack) {
		Intent i = new Intent(context, activity);
		
		i.putExtra(ExtrasConst.PRICE_ROW_ID_STR, priceRoid);
		i.putExtra(ExtrasConst.DOC_ROW_ID_STR, doc.getRowid());
		i.putExtra(INPACK_TAG, inPack);

		context.startActivity(i);		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Bundle b = savedInstanceState == null ? getIntent().getExtras() : savedInstanceState;
		if( b.containsKey(INPACK_TAG) )
			startInpack = b.getBoolean(INPACK_TAG);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if( startInpack != null )
			outState.putBoolean(INPACK_TAG, startInpack);
	}
	
	@Override
	protected boolean getStartInPack() {
		return (startInpack == null) ? super.getStartInPack() : startInpack;
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
			doOk();
			return true;
		}
		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case 212:
		case 221:
			doOk();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void doOk() {
		BtnOkR r = new BtnOkR();
		if( isInputValid(r) )
			r.run();
	}
}
