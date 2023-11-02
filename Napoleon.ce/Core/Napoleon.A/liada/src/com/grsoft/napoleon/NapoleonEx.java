package com.grsoft.napoleon;

import android.view.View;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.OrgImpl;

public class NapoleonEx extends Napoleon {
	@Override
	protected void setOrgBackground(int position, OrgImpl orgImpl, View view){
		view.setBackgroundResource(orgImpl != null && 
				(orgImpl.getData().flags & Org.FL_CUSTOM_FIZ) == Org.FL_CUSTOM_FIZ ? 
				R.drawable.list_navajowhite_selector :
					position % 2 != 0 ? R.drawable.even_row_selector :
										R.drawable.list_selector);
	}
}
