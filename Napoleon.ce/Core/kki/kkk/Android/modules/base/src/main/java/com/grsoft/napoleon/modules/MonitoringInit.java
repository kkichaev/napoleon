package com.grsoft.napoleon.modules;

import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.database.RcvNewHitching;
import com.grsoft.dataobjects.MonitoringItem;
import com.grsoft.napoleon.UpdateDBW;

public class MonitoringInit {
	public static void init(){
		UpdateDBW.addHitchingCtor(new HitchingCtor(){
			
			@Override
			public Hitching create() {
				return new RcvNewHitching(MonitoringItem.class, "MonitoringItem");
			}
		}, UpdateDBW.GEN_DATA_HITCHING);
	}
}
