package com.grsoft.napoleon;

import java.util.Date;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.TextView;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.CfgNplEx;
import com.grsoft.util.Consts;


public class DocumentsEx extends Documents {
	private static final String ID_ORG = "saved_id_org";
	private static final String ORG_TIME = "ORG_TIME";  
	@Override
	protected int getContentViewID() {
		return R.layout.documentsex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = (TextView) findViewById(R.id.tvText);
		tv.setText(((OrgEx)org.getData()).text);
	}
	
	@Override
	protected boolean isGpsPosValid() {
		boolean result = super.isGpsPosValid();
		
		if(!result){
			SharedPreferences sp =  getPreferences(Context.MODE_PRIVATE);
			String id = sp.getString(ID_ORG, "invalid_id_for_organization");
			
			if(id.equals(org.getData().id)){
				long time = sp.getLong(ORG_TIME, Consts.INVALID_ID);
				
				if(time != Consts.INVALID_ID){
					long now = new Date().getTime();
					CfgNplEx config = (CfgNplEx) ConfigManager.getConfig();
					
					result =  (now - time) < config.gps_valid_in_org; 
				}
				
			}
		}
		
		return result;
	}
	
	@Override
	protected void createNewDoc() {
		Editor ed = getPreferences(Context.MODE_PRIVATE).edit();
		ed.putString(ID_ORG, org.getData().id);
		ed.putLong(ORG_TIME, new Date().getTime());
		ed.commit();
		
		super.createNewDoc();
	}
}
