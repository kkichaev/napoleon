package com.grsoft.napoleon;

import android.os.Bundle;

public class DocumentsEx extends Documents {
	public static boolean SHOW_NOTES_ACTION = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SHOW_NOTES_ACTION = false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(SHOW_NOTES_ACTION){
			SHOW_NOTES_ACTION = false;
			initNotesDlg();
		}
			
	}
}
