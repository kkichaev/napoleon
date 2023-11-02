package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.OrgHighlightImpl;
import com.grsoft.dataobjects.impl.OrgImpl;
import android.view.View;
import android.widget.TextView;

public class NapoleonEx extends Napoleon {
	OrgHighlightImpl orgHighlight = new OrgHighlightImpl();
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		if(orgHighlight.read("id", oi.getData().id)){
			((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(getResources().getColor(R.color.red));
		}
	}
}
