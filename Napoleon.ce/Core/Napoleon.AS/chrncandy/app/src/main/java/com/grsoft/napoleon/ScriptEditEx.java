package com.grsoft.napoleon;

import com.grsoft.script.ScriptEdit;


public class ScriptEditEx extends ScriptEdit {
	@Override
	protected void onPause() {
		super.onPause();
		DocumentsEx.SHOW_NOTES_ACTION = true;
	}
}
