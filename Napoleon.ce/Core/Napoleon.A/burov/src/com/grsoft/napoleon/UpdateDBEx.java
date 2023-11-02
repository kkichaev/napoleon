package com.grsoft.napoleon;

import java.util.ArrayList;
import java.util.List;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.napoleon.documents.OrderWDoc;

public class UpdateDBEx extends UpdateDBPrint {
	@Override
	protected List<Hitching> getRestoreHitching() {
		List<Hitching> result = super.getRestoreHitching();
		
		if (result == null)
			result = new ArrayList<Hitching>();
		
		result.add(new DocumentRestore(OrderWDoc.instance()));
		return result;
	}
}
