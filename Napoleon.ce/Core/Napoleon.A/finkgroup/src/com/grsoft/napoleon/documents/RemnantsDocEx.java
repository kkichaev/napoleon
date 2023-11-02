package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.RemnantsImpl;

public class RemnantsDocEx extends RemnantsDoc {
	public RemnantsDocEx(String docName, String objName,
			Class<? extends RemnantsImpl> type) {
		super(docName, objName, type);
	}

	public static void initialize(Class<? extends RemnantsImpl> type) {
		if( instance != null )
			throw new RuntimeException("RemnantsDoc уже создан!");
		instance = new RemnantsDocEx("Сборка продукции", "Invoice", type);
	}
}
