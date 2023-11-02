package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.dataobjects.impl.ConfigImpl;

public class PotenzialOrgEx extends PotenzialOrg {
	private final static String ORG_TYPE = "ORG_TYPE";
	private Spinner spType;
	private EditText edRaduius;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ConfigImpl configImpl = new ConfigImpl();
		configImpl.getData().key = ORG_TYPE;
		configImpl.read();
		configImpl.close();
		
		String causes = configImpl.getData().value;
		
		List<String> list = new ArrayList<String>();
		
		if (causes.length() > 0)
			list.addAll(Arrays.asList(causes.split(";")));
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, R.layout.type_list_item, list);
		
		spType = (Spinner) findViewById(R.id.spType);
		spType.setAdapter(adapter);
		
		edRaduius = (EditText) findViewById(R.id.edRadius);
		
		if (appendMode || editMode){
			OrgEx oe = (OrgEx)orgImpl.getData();
			
			int index = -1;
			for(int i = 0; i < adapter.getCount(); i++)
				if (oe.btype.equals(adapter.getItem(i))){
					index = i;
					break;
				}
			
			if(index != -1)
				spType.setSelection(index);
			
			edRaduius.setText(Integer.toString(oe.radius));
		}
	}
	
	@Override
	protected int getContentViewId() {
		return R.layout.potenzial_orgex;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		super.onLocationChanged(location);
		((OrgEx)orgImpl.getData()).geocommit = 0;
	}
	
	@Override
	protected OKListener createOKListener() {
		return new OKListenerEx();
	}
	
	class OKListenerEx extends OKListener{
		
		@Override
		protected void postOnClick(Org org) {
			String rStr = edRaduius.getText().toString();
			int rVal = 0;
			
			if (rStr.length() > 0){
				try{
					rVal = Integer.parseInt(rStr);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			((OrgEx)org).radius = rVal;
			((OrgEx)org).btype = spType.getSelectedItem().toString();
		}
	}
}
