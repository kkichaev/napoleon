package com.grsoft.napoleon.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.grsoft.napoleon.Features;
import com.grsoft.util.OnClickListenerToNotify;

public class FindOnClickListener extends OnClickListenerToNotify {
	protected EditText findField;
//	protected AbsListView listView;
	FilterAdapter dbAdapter;
	View groupView;
	
	public FindOnClickListener(EditText findField, AbsListView listView, View groupView) {
		this.findField = findField;
		dbAdapter = (FilterAdapter)listView.getAdapter();
		this.groupView = groupView;
	}

	public FindOnClickListener(EditText findField, FilterAdapter adapter, View groupView) {
		this.findField = findField;
		dbAdapter = adapter;
		this.groupView = groupView;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		boolean shown = (groupView == null) ? findField.isShown() : groupView.isShown();

		if (shown) {
			resetFilter();
			
		    InputMethodManager imm = (InputMethodManager)findField.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		    imm.hideSoftInputFromWindow(findField.getWindowToken(), 0);
		} else
			beginFiltering();
		
		postOnClick(shown);
	}
	
	protected void postOnClick(boolean shown){};
	
	public void resetFilter() {
		setInputVisible(false);
		
//		dbAdapter = (FilterAdapter)listView.getAdapter();
		
		if(dbAdapter != null)
			dbAdapter.resetFilter();
	}
	
	public void beginFiltering() {
		setInputVisible(true);
		
		findField.setText("");
		
		if( Features.REQUSET_FOCUS_IN_SEARCH ) {
			InputMethodManager imm = (InputMethodManager)findField.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if( imm != null )
				imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
			findField.requestFocus();
		}
	}
	
	protected void setInputVisible(boolean show){
		if( groupView != null )
			groupView.setVisibility(show ? View.VISIBLE : View.GONE);
		else
			findField.setVisibility(show ? View.VISIBLE : View.GONE);
	}
}
