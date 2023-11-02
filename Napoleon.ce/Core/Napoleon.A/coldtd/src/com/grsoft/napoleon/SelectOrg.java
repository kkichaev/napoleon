package com.grsoft.napoleon;

import android.app.Dialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.AdapterView.OnItemClickListener;

import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.util.ExtrasConst;

public class SelectOrg extends NapoleonEx {	
	@Override
	protected OnItemClickListener getItemOnClickListner() {
		return new OrgSelecting();
	}

	class OrgSelecting extends OrglListOnClickListener {
		@Override
		protected void openOrg(OrgImpl oi) {
			Intent i = new Intent();
			i.putExtra(ExtrasConst.ORG_ID_STR, oi.getData().id);
			setResult(RESULT_OK, i);
			finish();
		}
	}
	
	@Override
	protected Dialog createMainMenuDlg() {
		return null;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
