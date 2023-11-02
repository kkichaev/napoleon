package com.grsoft.napoleon;

import com.grsoft.napoleon.util.CfgNpl;
import com.grsoft.napoleon.util.ConfigManager;

public class Warehouse3_62 extends WarehouseNew {
	@Override
	protected void onResume() {
		super.onResume();
		
		CfgNpl cfg = (CfgNpl) ConfigManager.getConfig();
		linesController.setLinesCount(cfg.linesCount);
	}
}
