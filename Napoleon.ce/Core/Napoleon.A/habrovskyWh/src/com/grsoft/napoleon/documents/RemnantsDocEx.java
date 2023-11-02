package com.grsoft.napoleon.documents;

import java.text.SimpleDateFormat;

import com.grsoft.dataobjects.impl.RemnantsImpl;

public class RemnantsDocEx extends RemnantsDoc {
	private SimpleDateFormat sdf =  new SimpleDateFormat("dd.MM.yy hh:mm");
	
	public RemnantsDocEx(String docName, String objName,
			Class<? extends RemnantsImpl> type) {
		super(docName, objName, type);
	}

	public static void initialize(Class<? extends RemnantsImpl> type) {
		if( instance != null )
			throw new RuntimeException("DebtDoc уже создан!");
		instance = new RemnantsDocEx(DOC_NAME, "Invoice", type);
	}

	@Override
	protected String getDateDocText(Document<?> doc) {
		return sdf.format(doc.getDate());
	}
}
