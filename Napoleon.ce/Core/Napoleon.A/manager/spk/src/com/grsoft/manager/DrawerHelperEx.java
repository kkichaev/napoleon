package com.grsoft.manager;

import com.grsoft.manager.spk.R;

import android.content.Context;

public class DrawerHelperEx extends DrawerHelper {
	@Override
	protected int getLeftMenuID() {
		return R.menu.main_navigate_ex;
	}
	
	@Override
	protected void childItemClick(Context context, int id) {
		super.childItemClick(context, id);
		
		if(id == R.id.task)
			SPKTaskActivity.open(context);
		else if (id == R.id.audit)
			AuditActivity.open(context);
	}
}
