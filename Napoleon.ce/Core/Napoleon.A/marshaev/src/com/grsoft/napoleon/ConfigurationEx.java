package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CurrentAgent;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.dataobjects.OrgEx;
import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.RefreshData;
import com.grsoft.network.RegisterService;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ConfigurationEx extends Configuration implements RegisterService.Handler, RefreshData.Handler {
	
	int selected = -1;
	
	@Override
	protected int getLayoutID() {
		return R.layout.configex;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		CfgNplEx cfg = (CfgNplEx)ConfigManager.getConfig();
		EditText ed = (EditText)findViewById(R.id.edAdmPassw);
		ed.setText(cfg.admPwd);
		
		View reg = findViewById(R.id.btnRegister);
		CurrentAgent ca = CurrentAgent.get(this);
		if( ca == null ) {
			findViewById(R.id.tvRegInfo).setVisibility(View.GONE);
		} else {
			//reg.setVisibility(View.GONE);			
			setInfo();
		}
		reg.setOnClickListener(new View.OnClickListener() {
			@Override public void onClick(View v) { doRegister(); }
		});
	}

	private void setInfo() {
		CurrentAgent ca = CurrentAgent.get(this);
		if( ca == null ) {
			return;
		}
		TextView info = (TextView)findViewById(R.id.tvRegInfo);
		info.setText(String.format("Код агента '%s'", ca.userid));
		
		selected = -1; 
		final CfgNplEx cfg = (CfgNplEx)ConfigManager.getConfig();
		final List<OrgEx> orgs = new ArrayList<OrgEx>();
		DataTraveler.travel(OrgEx.class, new DataTraveler.Travel<OrgEx>() {

			@Override
			public boolean travel(DataTraveler<OrgEx> item) {
				if( item.data.id.equals(cfg.orgId) )
					selected = orgs.size();
				
				orgs.add(item.data);
				item.data = new OrgEx();
				return true;
			}
		}, "", "name");
		
		ArrayAdapter<OrgEx> aa = new ArrayAdapter<OrgEx>(this, R.layout.simple_spinner_layout, orgs);
		aa.setDropDownViewResource(R.layout.simple_spinner_layout_drop_down);
		Spinner sp = (Spinner)findViewById(R.id.spOrg);
		sp.setAdapter(aa);
		if(selected >= 0)
			sp.setSelection(selected);
	}

	protected void doRegister() {
		save();
		RegisterService rs = new RegisterService(this, (CfgNplEx) ConfigManager.getConfig(), this);
		rs.execute((Void[])null);
	}

	@Override
	public void onCompleete(boolean result) {
		if( result ) {
			RcvNewHitching orgs = new RcvNewHitching(OrgEx.class, "CommonOrgs");
			RcvNewHitching agents = new RcvNewHitching(CurrentAgent.class, "Agents");
			
			List<Hitching> data = new ArrayList<Hitching>();
			data.add(orgs);
			data.add(agents);
			RefreshData rd = new RefreshData(this, this, data);
			rd.execute((Void[])null);
		}
	}
	
	@Override
	public void save() {
		CfgNplEx cfg = (CfgNplEx)ConfigManager.getConfig();
		EditText ed = (EditText)findViewById(R.id.edAdmPassw);
		cfg.admPwd = ed.getEditableText().toString();
		
		Spinner sp = (Spinner)findViewById(R.id.spOrg);
		OrgEx o = (OrgEx) sp.getSelectedItem();
		if( o != null )
			cfg.orgId = o.id; 
		super.save();
	}

	@Override
	public void onRead(boolean result) {
		if( result )
			runOnUiThread(new Runnable() {
				@Override public void run() { setInfo(); }
			});
	}
}
