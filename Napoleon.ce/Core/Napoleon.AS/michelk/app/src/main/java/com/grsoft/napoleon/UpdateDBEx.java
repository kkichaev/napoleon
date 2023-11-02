package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.Hitching;
import com.grsoft.database.PrezentHitching;
import com.grsoft.database.StoreUtils;
import com.grsoft.dataobjects.ServerInfo;
import com.grsoft.network.NetworkAsyncTask;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class UpdateDBEx extends UpdateDB {

	public static final String SERVER_3 = "MichelK3";
	public static final String SERVER_4 = "MichelK4";

	ServerInfo serverInfo = ServerInfo.read();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		((CheckBox)findViewById(R.id.cbVisit)).setChecked(true);
		CheckBox prezent = (CheckBox) findViewById(R.id.cbPresent);

		if (!BuildConfig.FLAVOR.equals("whDbf") ) {
			prezent.setChecked(true);
		}

		((CheckBox)findViewById(R.id.cbDebt)).setChecked(true);
		((CheckBox)findViewById(R.id.cbRemains)).setChecked(false);

		prezent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					if(serverInfo.name.length() == 0) {
						((CheckBox)findViewById(R.id.cbGenData)).setChecked(true);
					}
				}
			}
		});
	}
	
	@Override
	protected List<Hitching> getPrezentHitching() {
		serverInfo = ServerInfo.read();
		if(serverInfo.name.compareTo(SERVER_3) == 0 || serverInfo.name.compareTo(SERVER_4) == 0) {
			return super.getPrezentHitching();
		}

		List<Hitching> ret = new ArrayList<Hitching>();
		ret.add(new PrezentHitching());
		return ret;
	}
	
	@Override
	protected boolean onFinishUpdate(NetworkAsyncTask task) {
		if(((CheckBox)findViewById(R.id.cbPresent)).isChecked() &&
				(serverInfo.name.compareTo(SERVER_3) != 0 || serverInfo.name.compareTo(SERVER_4) == 0))
			StoreUtils.commitCRCChanges();
		return super.onFinishUpdate(task);
	}
}
