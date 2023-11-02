package com.grsoft.napoleon.documents;

import com.grsoft.util.Consts;
import com.grsoft.util.Util;

public class TotalSumConvertor {
	public String toString(long sum) {
		return Util.IntToScaleStr(sum, Consts.SUM_SCALE, Util.DEC_DELIM, false);
	}
}
