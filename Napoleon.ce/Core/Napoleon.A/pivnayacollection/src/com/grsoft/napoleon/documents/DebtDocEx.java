package com.grsoft.napoleon.documents;

import android.content.Context;
import com.grsoft.dataobjects.DeliveryEx;
import com.grsoft.dataobjects.impl.DeliveryImpl;
import com.grsoft.util.Util;

public class DebtDocEx extends DebtDoc {
	
	public static void initialize() {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new DebtDocEx();
	}

	@Override
	public int getViewTextColor(Context context, Document<?> doc) {
		if(doc instanceof DeliveryImpl) {
			return Util.GrServerColorToSystem(((DeliveryEx)doc.getData()).color);
		}
		return super.getViewTextColor(context, doc);
	}
}
