package com.grsoft.napoleon.documents;

import java.util.List;

import com.grsoft.dataobjects.ParamState;
import com.grsoft.dataobjects.impl.GoodsRestImpl;
import com.grsoft.napoleon.R;
import com.grsoft.network.DocExportListener;

public class GoodsRestDoc extends DocType implements DocItemsStock {

	public static final String DOC_NAME = "Подсчет остатков";
	static public final String OBJ_NAME = "GoodsRest";
	static private GoodsRestDoc instance = null;

	protected GoodsRestDoc() { super(DOC_NAME, GoodsRestImpl.class); }
	
	static public DocType instance() {
		if( instance == null )
			instance = new GoodsRestDoc();		
		return instance;
	}

	@Override
	public DocExportListener getDirtyDocuments() {
		CreatableDocument d = (CreatableDocument)create();
		return new DocSendListner(OBJ_NAME, d.getClass(), "params", ParamState.ofExported);
	}

	@Override
	public void getItemsFromLastDoc(String id, List<String> itemIds) { }

	@Override
	public int getResurceId() { return R.drawable.goods_doc; }
}
