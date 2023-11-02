package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainEx extends Main implements OnClickListener {
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
//		if (btnMode != null)
//			btnMode.setVisibility(View.GONE);
		
		tvTotalSum.setOnClickListener(this);
		
		SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
		Editor ed = pref.edit();
		ed.putInt(PERIOD_TYPE, 2);
		ed.commit();
	}
	
	@Override
	public void openOrg(Org org, int pos) {
		if(VersionChecker.isLastVersion(this, false) == false)
			showDialog(R.id.need_update_version);
		else
			super.openOrg(org, pos);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		if( id == R.id.need_update_version)
			return VersionChecker.createAlertDialog(this); 
		return super.onCreateDialog(id);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == tvTotalSum.getId())
			DocListSimple.openDocList(this);
		
	}
}
