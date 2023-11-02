package com.grsoft.napoleon;

import java.util.List;

import com.grsoft.database.DataObjectRestore;
import com.grsoft.database.DbWriter;
import com.grsoft.database.Hitching;
import com.grsoft.dataobjects.DailyRoute;
import com.grsoft.dataobjects.DataObjectInfo;
import com.grsoft.network.exception.RuntimeException;

public class UpdateDBEx extends UpdateDB {
	@Override
	protected List<Hitching> getGenDataHitchings() throws RuntimeException {
		List<Hitching> ret = super.getGenDataHitchings();
		ret.add(new DailyRouteHitching());
		return ret;
	}
}

class DailyRouteHitching extends DataObjectRestore {
	public DailyRouteHitching() {
		super(DailyRoute.class, "DailyRoute", "date");
		DbWriter.dropTable(DataObjectInfo.getInstance().getTableName(DailyRoute.class));		
	}
}

