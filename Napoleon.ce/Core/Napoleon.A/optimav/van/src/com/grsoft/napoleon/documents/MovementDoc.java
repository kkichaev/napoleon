package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.MovementImpl;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.R;

public class MovementDoc extends OrderDoc{
	static public final String DOC_NAME = "Перемещение ТМЦ";
	static public final String OBJ_NAME = "Movement";
	static protected MovementDoc instance = null;
	
	protected MovementDoc(Class<? extends OrderImplBase<? extends Order>> retClass) { super(DOC_NAME, OBJ_NAME, retClass); }

	static public DocType instance() {
		if( instance == null ) {
			instance = new MovementDoc(MovementImpl.class);
		}
		
		return instance;
	}

	static public DocType instance(Class<? extends OrderImplBase<? extends Order>> retClass) {
		instance = new MovementDoc(retClass);
		return instance;
	}
	
	@Override public int getResurceId() { return R.drawable.return_doc; }
	
	@Override
	public int getDocTitle() {
		return R.string.movement_list;
	}
}
