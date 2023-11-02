package com.grsoft.napoleon;

import com.grsoft.database.DataBaseManager;
import com.grsoft.database.DbWriter;
import com.grsoft.dataobjects.OrgSum;
import com.grsoft.napoleon.util.debug.Path;

import android.os.Bundle;
import android.view.View;

public class ConfigurationEx extends Configuration {
	protected int getLayoutID() { return  R.layout.config_ex; }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		findViewById(R.id.btnClear).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Path.clearDataDir();
				DataBaseManager.clearBase();
				DbWriter.checkDBTable(OrgSum.class);
			}
		});
	}
}
