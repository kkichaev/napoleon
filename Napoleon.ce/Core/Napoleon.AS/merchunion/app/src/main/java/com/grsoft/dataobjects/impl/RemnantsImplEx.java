package com.grsoft.dataobjects.impl;

import com.grsoft.napoleon.RemnantsEdit;

import android.content.Context;

public class RemnantsImplEx extends RemnantsImpl {
	@Override
	public void editItem(long itemRowid, Context context ) {
		RemnantsEdit.open(context, getRowid(), itemRowid); 
	}
}	
