package com.grsoft.napoleon;

import com.grsoft.napoleon.documents.CreatableDocument;
import com.grsoft.napoleon.documents.Document;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;


public class DocListEx extends DocList {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(btnDelete != null)
			btnDelete.setVisibility(View.GONE);
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		Document<?> d = (Document<?>) adapter.getItem(((AdapterContextMenuInfo)menuInfo).position);
		
		if(d instanceof CreatableDocument<?>){
			CreatableDocument<?> cd = (CreatableDocument<?>)d;
			
			if(cd.isExported())
				menu.removeItem(R.id.itDelete);
		}
	}
}	
