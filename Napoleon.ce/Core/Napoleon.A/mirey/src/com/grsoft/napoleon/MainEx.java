package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.ConfigImpl;

import android.view.View;

public class MainEx extends Main {
	public static boolean hardMode = false;
	
	@Override
	protected void onResume() {
		super.onResume();
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		if (cfg.getValue(sb, "HardRoute") && sb.toString().equals("1")) {
			hardMode = true;
			OrgHelper.refresh(((FoldersMainAdapter)foldersMainAdapter).getTodayItems());
		}else
			hardMode = false;
	}
	
	@Override
	protected void setOrgBackground(int pos, Org org, View v) {
		super.setOrgBackground(pos, org, v);
		
		if(hardMode && org != null && !OrgHelper.isEnabled(org.id))
			v.setBackgroundResource(R.drawable.list_disable_selector);
	}
}
