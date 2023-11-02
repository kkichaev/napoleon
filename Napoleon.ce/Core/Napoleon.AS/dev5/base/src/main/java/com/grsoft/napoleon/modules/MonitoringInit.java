package com.grsoft.napoleon.modules;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.napoleon.UpdateDB;

public class MonitoringInit {
	public static void init(){
		UpdateDB.addHitchingCtor(new HitchingCtor(){
			
			@Override
			public Hitching create() {
				return new RcvNewHitching(MonitoringItem.class, "MonitoringItem");
			}
		}, UpdateDB.GEN_DATA_HITCHING);
	}
}
