package com.grsoft.napoleon;

import java.util.ArrayList;

import com.grsoft.dataobjects.Config;
import com.grsoft.dataobjects.OrderEx;
import com.grsoft.dataobjects.impl.ConfigImpl;
import com.grsoft.dataobjects.impl.OrderImpl;
import com.grsoft.napoleon.documents.Document;
import com.grsoft.util.view.dialog_helper.DialogHelper;
import com.grsoft.util.view.dialog_helper.KeyValue;

public class OrderHelper {

	public static String getSumType(Document<?> document) {
		ConfigImpl config = new ConfigImpl();
		Config c = config.getData();
		c.key = "¬ид÷ены";
		config.read();
		config.close();

		String sel = document instanceof OrderImpl ? ((OrderEx)document.getData()).prcType : "";
		ArrayList<KeyValue> values = new ArrayList<KeyValue>();
		int selCost = DialogHelper.makeListWithKey(c.value, values, sel);
		
		if( selCost < 0 && values.size() > 0)
			selCost = 0;

		return selCost >= 0 ? values.get(selCost).value.toString() : "";
	}
}
