package com.grsoft.napoleon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.View;
import android.widget.TextView;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgDogovor;
import com.grsoft.dataobjects.OrgExtended;
import com.grsoft.dataobjects.impl.OrgImpl;


public class NapoleonEx extends Napoleon {
	private Map<String, Integer> orgColors = new HashMap<String, Integer>();
	
	@Override
	protected void onResume() {
		super.onResume();
		orgColors.clear();
	}
	
	@Override
	protected void drawOrg(OrgImpl oi, View view) {
		super.drawOrg(oi, view);
		
		if (!orgColors.containsKey(oi.getData().id))
			initOrgColor(oi.getData(), view);
		
		((TextView)view.findViewById(R.id.tvOrgName)).setTextColor(orgColors.get(oi.getData().id));
	}
	
	protected String getOrgReadingFields() { return "name,id,address,color,flags,dogovors"; }

	private void initOrgColor(Org data, View view) {
		TextView tv = (TextView)view.findViewById(R.id.tvOrgName);
		int c = tv.getCurrentTextColor();
		
		OrgExtended e = (OrgExtended)data;
		List<OrgDogovor> d = e.getDogovors();
		
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
