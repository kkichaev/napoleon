package com.grsoft.napoleon.documents;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.napoleon.R;
import com.grsoft.napoleon.UpdateDBW;

public class ReturnOnDeliveryDoc extends ReturnDoc {
	private static final String THE_OBJ_NAME = "ReturnOnDelivery";
	private static ReturnOnDeliveryDoc theInstance = null;
	
	protected ReturnOnDeliveryDoc(Class<? extends OrderImplBase<? extends Order>> retClass) { 
		super(THE_OBJ_NAME, THE_OBJ_NAME, retClass); 
	}
	
	@Override
	public int getDocTitle() {
		return R.string.returnondelivery_doc_title;
	}
	
	static public DocType theInstance(Class<? extends OrderImplBase<? extends Order>> retClass) {
//
		if( theInstance == null ) {
			theInstance = new ReturnOnDeliveryDoc(retClass);
	
			UpdateDBW.addHitchingCtor(new HitchingCtor() {
				@Override public Hitching create() { return new DocumentRestore(instance, instance.objName); }
			}, UpdateDBW.RESTORE_DATA_HITCHING);
		}
		return theInstance;
	}
}
