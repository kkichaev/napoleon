/*
 * Copyright (C), 2011, Гильдия Разработчиков
 *
 * Remnants(Остатки) - документ
 *
 * kki   11/04/2011   creating
 */
package com.grsoft.napoleon.documents;

import java.util.List;

import com.grsoft.dataobjects.impl.RemnantsImpl;
import com.grsoft.napoleon.R;

/**
 * Документ - Остатки
 * @author kki
 *
 */
public class RemnantsDoc extends DateDocType 
	implements DocItemsStock{
	static public final String DOC_NAME = "Остатки";
	static public final String OBJ_NAME = "OrgRemnants";
	static protected RemnantsDoc instance = null;
	
	protected RemnantsDoc() { this(DOC_NAME, OBJ_NAME, RemnantsImpl.class); }
	
	public RemnantsDoc(String docName, String objName,
			Class<? extends RemnantsImpl> type) {
		super(docName, objName, type);
	}

	static public DocType instance(Class<? extends RemnantsImpl> type) {
		if(instance == null)
			instance = new RemnantsDoc(DOC_NAME, OBJ_NAME, type);
		return instance;
	}

	static public DocType instance() {
		if( instance == null )
			instance = new RemnantsDoc();
		return instance;
	}

	@Override
	public void getItemsFromLastDoc(String id, List<String> itemIds, int period) {
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.remnants_doc;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.remnants_doc_2;
	}
	
	@Override
	public int getDocTitle() {
		return R.string.remains_doc_title;
	}
}
