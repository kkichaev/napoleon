package com.grsoft.napoleon.documents;

import com.grsoft.dataobjects.impl.BonusImpl;
import com.grsoft.napoleon.R;

import android.app.Activity;
import android.view.View;

public class BonusDoc extends DocType {
	static public final String OBJ_NAME = "Bonus";
	static protected BonusDoc instance = null;
	
	protected BonusDoc() {
		super(OBJ_NAME, OBJ_NAME, BonusImpl.class);
	}
	
	@Override
	public int getDocTitle() {
		return R.string.bonus_doc_title;
	}

	public static DocType instance() {
		if( instance == null )
			instance = new BonusDoc();
		
		return instance;
	}

	@Override
	public int getResurceId() {
		return R.drawable.bonus;
	}
	
	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);
		
		View v = documentsView.findViewById(R.id.btnNewDoc);
		
		if (v != null)
			v.setVisibility(View.GONE);
	}
}
