package com.grsoft.napoleon;

import android.widget.Toast;

import com.grsoft.dataobjects.OrgEx;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class DocumentsEx extends Documents {
	@Override
	protected void onResume() {
		super.onResume();
		
		if(org != null){
			OrgEx orgEx = (OrgEx) org.getData();
			
			if (orgEx.stopMsg.trim().length() > 0)
				Toast.makeText(this, orgEx.stopMsg, Toast.LENGTH_LONG).show();
			else if (orgEx.limit > 0)
				Toast.makeText(this, String.format("Лимит по отгрузкe: %s", 
						Util.IntToScaleStr(orgEx.limit, Consts.SUM_SCALE)), 
						Toast.LENGTH_LONG).show();
		}
	}
}
