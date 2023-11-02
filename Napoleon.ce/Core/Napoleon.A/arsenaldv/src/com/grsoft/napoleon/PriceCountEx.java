package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNplEx;
import com.grsoft.napoleon.util.ConfigManager;

public class PriceCountEx extends PriceCount {
	@Override
	protected boolean getStartInPack() {
		return ((CfgNplEx)ConfigManager.getConfig()).packInput;
	}
}
