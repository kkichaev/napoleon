package com.grsoft.napoleon;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;

import android.os.Bundle;
import android.widget.EditText;

public class PotenzialOrgEx extends PotenzialOrg {
	EditText edInn;
	
	@Override protected int getContentViewId() { return R.layout.potenzial_org_ex; }
	@Override protected OKListener createOKListener() { return new OKListenerEx(); }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		edInn = (EditText)findViewById(R.id.edInn);
		edInn.setText(((OrgEx)orgImpl.getData()).orgCreateInn);
	}
	
	class OKListenerEx extends OKListener {
		@Override
		protected void postOnClick(Org org) {
			((OrgEx)org).orgCreateInn = edInn.getText().toString().trim();
		}
	}
}
