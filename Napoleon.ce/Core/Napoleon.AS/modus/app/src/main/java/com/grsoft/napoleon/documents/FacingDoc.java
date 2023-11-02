package com.grsoft.napoleon.documents;

import android.app.Activity;
import android.view.View;
import android.widget.Adapter;
import android.widget.TextView;

import com.grsoft.dataobjects.impl.FacingImpl;
import com.grsoft.napoleon.R;

public class FacingDoc extends DocType {
	public static final String OBJ_NAME = "Facing";
	public static FacingDoc instance = null;
	
	protected FacingDoc() {	super(OBJ_NAME, OBJ_NAME, FacingImpl.class);}
	
	static public DocType instance(){
		if(instance == null)
			instance = new FacingDoc();
		
		return instance;
	}
	
	@Override
	public int getResurceId() {
		return R.drawable.facing;
	}
	
	@Override
	public int getResurce2Id() {
		return R.drawable.facing2;
	}

	@Override
	public int getDocTitle() {
		return R.string.facing_doc;
	}

	@Override
	public void viewOpened(Activity documentsView) {
		super.viewOpened(documentsView);

		TextView tvSumColumnTitle = (TextView) documentsView
				.findViewById(R.id.SumColumnTitle);

		if (tvSumColumnTitle != null)
			tvSumColumnTitle.setVisibility(View.GONE);

		View v = documentsView.findViewById(R.id.tvSum);

		if (v != null)
			v.setVisibility(View.INVISIBLE);
	}

	@Override
	public void setView(Adapter adapter, View view, Document<?> doc) {
		super.setView(adapter, view, doc);

		TextView tvSum = (TextView)view.findViewById(R.id.tvSum);
		tvSum.setVisibility(View.GONE);
	}
}
