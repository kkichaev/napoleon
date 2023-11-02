package com.grsoft.napoleon.documents;
import com.grsoft.aceteam.R;

import com.grsoft.database.DocumentRestore;
import com.grsoft.database.Hitching;
import com.grsoft.database.HitchingCtor;
import com.grsoft.dataobjects.Order;
import com.grsoft.dataobjects.impl.OrderImplBase;
import com.grsoft.dataobjects.impl.ReturnImpl;
import com.grsoft.napoleon.UpdateDB;

public class ReturnDoc extends OrderDoc {

	static public final String DOC_NAME = "Возвраты";
	static public final String OBJ_NAME = "Returns";
	static protected ReturnDoc instance = null;
	
	protected ReturnDoc(Class<? extends OrderImplBase<? extends Order>> retClass) { super(DOC_NAME, OBJ_NAME, retClass); }

	static public DocType instance() {
		if( instance == null ) {
//			DataObjectInfo doi = DataObjectInfo.getInstance(); 
//			doi.replaceTableName(Return.class, "Return");
//
			instance = new ReturnDoc(ReturnImpl.class);

			UpdateDB.addHitchingCtor(new HitchingCtor() {
				@Override public Hitching create() { return new DocumentRestore(instance, instance.objName); }
			}, UpdateDB.RESTORE_DATA_HITCHING);
		}
		return instance;
	}

	static public DocType instance(Class<? extends OrderImplBase<? extends Order>> retClass) {
//		DataObjectInfo doi = DataObjectInfo.getInstance(); 
//		doi.replaceTableName(Return.class, "Return");
//
		if( instance == null ) {
			instance = new ReturnDoc(retClass);
	
			UpdateDB.addHitchingCtor(new HitchingCtor() {
				@Override public Hitching create() { return new DocumentRestore(instance, instance.objName); }
			}, UpdateDB.RESTORE_DATA_HITCHING);
		}
		return instance;
	}
	
	protected ReturnDoc(String name, String objName,   Class<? extends OrderImplBase<? extends Order>> type) {
		super(name, objName, type);
	}
	
//	@Override
//	public void getItemsFromLastDoc(String id, List<String> itemIds) {
//	}
	
	@Override public int getResurceId() { return R.drawable.return_doc; }
	@Override public int getResurce2Id() { return R.drawable.return_doc_2; }
	
	@Override public boolean outOfScript() { return false; }
	
	@Override public int getDocTitle() { return R.string.return_doc_title; }
}
