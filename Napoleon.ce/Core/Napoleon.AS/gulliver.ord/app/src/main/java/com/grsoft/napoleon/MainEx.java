package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgEx;


public class MainEx extends Main {
	private Map<String, Integer> orgColors = new HashMap<String, Integer>();
	
	@Override
	protected void onResume() {
		super.onResume();
		orgColors.clear();
	}

	@Override
	protected void drawOrg(Org org, View view) {
		super.drawOrg(org, view);

		if (!orgColors.containsKey(org.id))
			initOrgColor(org, view);

		((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(orgColors.get(org.id));
	}

	private void initOrgColor(Org data, View view) {
		TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
		int c = tv.getCurrentTextColor();
		
		OrgEx e = (OrgEx)data;
		List<OrgDogovor> d = e.dogovors;
		
		if( d != null && d.size() > 0){
			boolean sd = false;
			boolean ad = true;
			
			for(int i = 0; i < d.size(); i++){
				String m = d.get(i).stopMsg.trim();
			
				if (!sd && m.length() > 0)
					sd = true;
				
				if(ad && m.length() == 0)
					ad = false;
			}
			
			if (ad)
				orgColors.put(data.id, getResources().getColor(R.color.red));
			else if(sd)
				orgColors.put(data.id, getResources().getColor(R.color.blue));
			else
				orgColors.put(data.id, c);
		}else
			orgColors.put(data.id, c);
	}
}
