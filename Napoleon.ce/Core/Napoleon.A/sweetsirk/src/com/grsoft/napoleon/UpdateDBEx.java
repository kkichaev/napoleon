package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.network.LoginData;
import com.grsoft.network.UserInfo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected void initilizeUIComponent() {
		super.initilizeUIComponent();
		
		Button button = (Button) findViewById(R.id.btnUpdate);
		button.setOnClickListener(new StartUpdatesListener() {
			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox) findViewById(R.id.cbPresent);
				
				if (cb != null && cb.isChecked() && syncGSM())
					Toast.makeText(v.getContext(), R.string.connect_to_wifi, Toast.LENGTH_SHORT).show();
				else
					super.onClick(v);
			}
			
		});
		
		((CheckBox) findViewById(R.id.cbDebt)).setChecked(true);
	}

	protected boolean syncGSM() {
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return !mWifi.isConnected();
	}
	
	protected UserInfo getRcvUserInfo() {
		return addIp3Info(super.getRcvUserInfo());
	}

	protected UserInfo getSndUserInfo() {
		return addIp3Info(super.getSndUserInfo());
	}

	protected UserInfo getGpsUserInfo() {
		return addIp3Info(super.getGpsUserInfo());
	}
	
	protected UserInfo addIp3Info(UserInfo info) {
		CfgNplEx cfg = (CfgNplEx) ConfigManager.getConfig();
		UserInfo.ConnArg conn = new UserInfo.ConnArg();
		conn.address = cfg.address3;
		conn.port = cfg.port3;
		info.addConnArg(conn);
		
		return info;
	}
}
