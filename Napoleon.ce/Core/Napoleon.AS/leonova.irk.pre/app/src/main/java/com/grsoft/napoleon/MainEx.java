package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

public class MainEx extends Main{
	@Override
	protected void setOrgBackground(int pos, Org org, View v) {
		super.setOrgBackground(pos, org, v);

		if(org != null) {
			if (((OrgEx)org).merc == 0 && ((OrgEx)org).chznak == 0)
				v.setBackgroundResource(R.drawable.mercchznak_selector);
			else if (((OrgEx)org).merc == 0)
				v.setBackgroundResource(R.drawable.merc_selector);
			else if (((OrgEx)org).chznak == 0)
				v.setBackgroundResource(R.drawable.chznak_selector);
		}
	}
}
