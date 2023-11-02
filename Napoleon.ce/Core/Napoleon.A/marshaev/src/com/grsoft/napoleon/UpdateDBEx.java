package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;

import com.grsoft.database.DbWriter;
import com.grsoft.database.FullPrice;
import com.grsoft.database.Hitching;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.CurrentAgent;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.dataobjects.Folder;
import com.grsoft.dataobjects.Matrix;
import com.grsoft.dataobjects.Org;
import com.grsoft.dataobjects.impl.DbObject;
import com.grsoft.napoleon.util.Config;
import com.grsoft.napoleon.util.ConfigManager;
import com.grsoft.napoleon.util.ProgID;
import com.grsoft.network.ServerCommand;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> data = new ArrayList<Hitching>();
		data.add(new RcvNewHitching(Matrix.class, "CommonMatrix"));
		data.add(new RcvNewHitching(DbObject.getDataType(Folder.class), "Folder"));
		data.add(new FullPriceEx());

		data.add(new RcvNewHitching(CurrentAgent.class, "Agents"));
		data.add(new RcvNewHitching(Org.class, "CommonOrgs"));
		return data;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ServerCommand.DeviceID = ProgID.getPrgID(this);
		Config cfg = ConfigManager.getConfig();
		cfg.login = "";
		cfg.passw = "";
		cfg.impersonate = "";
		
		int[] ids = new int[] {R.id.cbRemains, R.id.cbVisit, R.id.cbPresent, R.id.cbCost, R.id.cbDebt};
		for(int id : ids) {
			View v = findViewById(id);
			if( v != null )
				v.setVisibility(View.GONE);
		}
	}
}

class FullPriceEx extends FullPrice {
	@Override
	public void prepareReading() {
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(dataObject));
		super.prepareReading();
	}
}
