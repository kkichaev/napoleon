package com.grsoft.napoleon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.grsoft.database.DbReader;
import com.grsoft.dataobjects.AgentPrefix;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.util.ExtrasConst;

public class PotenzialOrgEx extends PotenzialOrg {
	private final static String REGIONID = "regionid";
	private String regionid = "";
	
	public static void open(Context context, String regionid){
    	Intent intent = new Intent(context, activity); 
    	intent.putExtra(APPEND_STR, true);
    	intent.putExtra(REGIONID, regionid);
		context.startActivity(intent);
    }
	
	 public static void open(Context context, long rowid, boolean editable, String regionid){
	    	Intent intent = new Intent(context, activity);
	    	intent.putExtra(ExtrasConst.ORG_ID_STR, rowid);
	    	intent.putExtra(EDIATBLE_STR, editable);
	    	intent.putExtra(REGIONID, regionid);
			context.startActivity(intent);
	    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		regionid = getIntent().getExtras().getString(REGIONID);
		
		findViewById(R.id.linearLayout3).setVisibility(View.GONE);
		findViewById(R.id.linearLayout6).setVisibility(View.GONE);
		
		LinearLayout ll = ((LinearLayout)findViewById(R.id.linearLayout1));
		LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) ll.getLayoutParams();
		lp.weight = 1;
	}
	
	@Override
	protected OKListener createOKListener() {
		return new OKListener(){
			
			@Override
			protected String genOrgId(){
				return getAgentPrefix() + super.genOrgId();
			}
			
			@Override
			protected void postOnClick(Org org) {
				OrgEx orgEx = (OrgEx) org;
				orgEx.region = regionid;
			}
		};
	}
	
	public static String getAgentPrefix(DbReader r) {
		String result = "";
		Config config = ConfigManager.getConfig();
		AgentPrefix ap = new AgentPrefix();
		String agentTable = DataObjectInfo.getInstance().getTableName(ap.getClass());
		boolean bdo = r.select(ap, agentTable, "login='" + config.login
				+ "' and password='" + config.passw + "'" );
		
		if( bdo )
			result = ap.prefix;
		r.close();
		
		return result;
	}
	
	public static String getAgentPrefix(){
		DbReader r = new DbReader();
		String result = getAgentPrefix(r);
		r.close();
		return result;
	}
}
