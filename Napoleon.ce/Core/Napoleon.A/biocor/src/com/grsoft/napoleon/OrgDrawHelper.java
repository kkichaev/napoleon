package com.grsoft.napoleon;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;


public class OrgDrawHelper {
	public static View draw(Context c, Org o, View view){
		if (view == null)
			view = View.inflate(c, R.layout.main_list_rowex, null);
		
		OrgEx oe = (OrgEx)o;
		
		View v = view.findViewById(R.id.ivFolder);
		v.setVisibility(View.GONE);
		
		TextView tv = (TextView) view.findViewById(R.id.tvOrgName);
		tv.setText(orgName(oe));
		
		return view;
	}

	private static CharSequence orgName(OrgEx oe) {
		return oe.name;
	}
}
