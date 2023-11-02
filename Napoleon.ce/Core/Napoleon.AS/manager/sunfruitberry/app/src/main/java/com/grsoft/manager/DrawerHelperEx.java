package com.grsoft.manager;

import android.content.Context;

public class DrawerHelperEx extends DrawerHelper {
	@Override
	protected int getLeftMenuID() {
		return R.menu.main_navigate_ex;
	}
	
	@Override
	protected void childItemClick(Context context, int id) {
		if(id == R.id.agent_memo) {
			AgentMemo.open(context);
		}
	}
}
