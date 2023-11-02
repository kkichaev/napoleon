package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.Arrays;

import com.grsoft.napoleon.plans.Plans;

public class NapoleonEx extends Napoleon {
	@Override
	protected CharSequence[] getDocumentsList() {
		ArrayList<CharSequence> list = new ArrayList<CharSequence>(
				Arrays.asList(super.getDocumentsList()));
		
		list.add("Планы");
		
		CharSequence[] result = new CharSequence[list.size()];
		list.toArray(result);
		return result;
	}
	
	@Override
	protected void onOpenDocumentsList(int which) {
		if (which == 3)
			Plans.open(this);
		else
			super.onOpenDocumentsList(which);
	}
}
