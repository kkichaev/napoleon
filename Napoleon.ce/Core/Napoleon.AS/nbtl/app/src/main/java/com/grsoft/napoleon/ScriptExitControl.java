package com.grsoft.napoleon;

import com.grsoft.dataobjects.impl.ConfigImpl;

public class ScriptExitControl {
	public boolean allowExit() {
		final String KEY = "ExitControl";
		final String TRUE = "True";
		
		StringBuilder sb = new StringBuilder();
		ConfigImpl cfg = new ConfigImpl();
		cfg.getValue(sb, KEY);
		
		return !sb.toString().equals(TRUE);
	}
}
