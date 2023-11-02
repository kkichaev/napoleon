package com.grsoft.dataobjects.impl;

import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import com.grsoft.dataobjects.BonusDef;
import com.grsoft.dataobjects.DataTraveler;
import com.grsoft.util.Util;

public class BonusDefImpl extends DbObject<BonusDef> {
	/**
	 * КодТовара => Бонус
	 * @return 
	 */
	@SuppressLint("DefaultLocale")
	public static HashMap<String, BonusDef> getActiveBonuses(Date date) {
		final HashMap<String, BonusDef> ret = new HashMap<String, BonusDef>();
		if( date != null ) {
			date = Util.getDayStart(date);
			String where = String.format("start <= %d and till >= %d", date.getTime(), date.getTime());
			DataTraveler.travel(BonusDef.class, new DataTraveler.Travel<BonusDef>() {
	
				@Override
				public boolean travel(DataTraveler<BonusDef> item) {
					ret.put(item.data.iditem, item.data);
					item.data = new BonusDef();
					return true;
				}
			}, where);
		}
		return ret;
	}
}
