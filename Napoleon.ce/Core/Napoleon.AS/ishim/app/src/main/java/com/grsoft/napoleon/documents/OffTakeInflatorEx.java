package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.napoleon.documents.OffTakeHistory.OffTakeInflator;
import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class OffTakeInflatorEx extends OffTakeInflator {
	@Override
	public int getOffTake() {
		ConfigImpl config = new ConfigImpl();
		config.getData().key = "OffTakeCoef";
		boolean readed = config.read();
		config.close();
		
		int coef = 0;
		if( readed ) 
			coef = (int) Util.StrToScale(config.getData().value, Consts.SUM_SCALE);
	
		return (coef == 0) ? super.getOffTake() : coef;
	}
}
