package com.grsoft.napoleon.documents;

import java.util.Date;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.DataObject;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.OrgImpl;
import com.grsoft.napoleon.R;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}

	@Override
	protected String getOrgWhere(String orgId) {
		if(orgId == null) return "";
		
		OrgImpl org = new OrgImpl();
		org.read("id", orgId);

		StringBuilder result = new StringBuilder();
		result.append("id='").append(((OrgEx)org.getData()).ido).append("'");
		return result.toString();
	}
}
