package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.MovementWhImpl;
import com.grsoft.napoleon.R;

public class MovementDoc extends DateDocType {
	static MovementDoc instance = null;
	
	MovementDoc() {
		super("Перемещение товара", "MovementWh", MovementWhImpl.class);
	}
	
	static public DocType instance() {
		if( instance == null )
			instance = new MovementDoc();
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.move_doc; }
}
