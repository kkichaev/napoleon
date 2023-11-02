package com.grsoft.napoleon;

import android.os.Bundle;
import com.grsoft.dataobjects.VisitEx;


public class VisitEditEx extends VisitEditNew {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(((VisitEx)visit.getData()).actgs > 0){
			edNotes.setEnabled(false);
		}
	}
}
