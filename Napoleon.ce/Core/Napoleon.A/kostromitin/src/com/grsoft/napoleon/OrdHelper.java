package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OrdHelper {
	public static int getMaxDiscount() {
		int maxDsc = 500;
		StringBuilder sb = new StringBuilder();
		ConfigImpl ci = new ConfigImpl();
		if(ci.getValue(sb, "МаксимальнаяСкидка")) {
			maxDsc = Util.StrToScale(sb.toString(), Consts.SUM_SCALE);
		}
		return maxDsc;
	}
}
